package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.common.fx.dialog.OptionDialog
import com.github.windchopper.common.fx.dialog.OptionDialogModel
import javafx.event.EventTarget
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.stage.Modality
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

    private fun alert(alertType: OptionDialog.Type, message: String) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            OptionDialog.showOptionDialog(message, alertType, listOf(OptionDialogModel.Option.OK),
                prepareStageDialogFrame(Globals.logoImage, Modality.WINDOW_MODAL, false))
        }
    }

    fun informationAlert(message: String) {
        alert(OptionDialog.Type.INFORMATION, message)
    }

    fun errorAlert(message: String) {
        alert(OptionDialog.Type.ERROR, message)
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

