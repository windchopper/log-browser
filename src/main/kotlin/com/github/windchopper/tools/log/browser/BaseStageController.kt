package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.StageFormController
import javafx.event.EventTarget
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.logging.Level
import java.util.logging.Logger

@WeldAware abstract class BaseStageController: StageFormController() {

    companion object {
        val logger = Logger.getLogger(BaseStageController::class.qualifiedName)
    }

    override fun afterLoad(form: Parent, parameters: Map<String?, *>, formNamespace: Map<String?, *>) {
        super.afterLoad(form, parameters, formNamespace)
        stage.icons.add(Globals.logoImage)
    }

    private fun alert(alertType: AlertType, message: String) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            Alert(alertType, message, ButtonType.OK).show()
        }
    }

    fun informationAlert(message: String) {
        alert(AlertType.INFORMATION, message)
    }

    fun errorAlert(message: String) {
        alert(AlertType.ERROR, message)
    }

    fun errorLogAndAlert(exception: Throwable) {
        val errorMessage = ExceptionUtils.getRootCauseMessage(exception)
        logger.log(Level.SEVERE, errorMessage, exception)
        errorAlert(errorMessage)
    }

    private fun topLevelStage(currentStage: Stage): Stage {
        val topLevelStageCandidate = Stage.getWindows()
            .filterIsInstance<Stage>()
            .firstOrNull { it.owner === currentStage }

        return topLevelStageCandidate
            ?.let { topLevelStage(it) }
            ?:currentStage
    }

    fun EventTarget.blockingAction(vararg disableEventTargets: EventTarget, blockingAction: () -> Unit): Job {
        val cursorProperty = when (this) {
            is Node -> this.cursorProperty(); is Scene -> this.cursorProperty(); is Window -> this.scene.cursorProperty(); else -> null
        }

        val disableProperties = disableEventTargets.mapNotNull {
            when (it) {
                is Node -> it.disableProperty(); is MenuItem -> it.disableProperty(); else -> null
            }
        }

        cursorProperty?.set(Cursor.WAIT)
        disableProperties.forEach { it.set(true) }

        val job = GlobalScope.launch {
            try {
                blockingAction()
            } finally {
                disableProperties.forEach { it.set(false) }
                cursorProperty?.set(Cursor.DEFAULT)
            }
        }

        job.invokeOnCompletion {
            it?.let {
                errorLogAndAlert(it)
            }
        }

        return job
    }

}

