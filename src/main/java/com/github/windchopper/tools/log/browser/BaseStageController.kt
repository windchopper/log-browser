package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.FormController
import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.common.fx.dialog.OptionDialog
import com.github.windchopper.common.fx.dialog.OptionDialogModel
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.logging.Level

abstract class BaseStageController: StageFormController() {

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

    fun errorLogAndAlert(exception: Exception?) {
        val errorMessage = ExceptionUtils.getRootCauseMessage(exception)
        FormController.logger.log(Level.SEVERE, errorMessage, exception)
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

    fun launchWithWaitCursor(vararg disableNodes: NodeOrMenuItem, action: () -> Unit) {
        stage.scene.cursor = Cursor.WAIT
        disableNodes.forEach { it.disabled = true }

        GlobalScope.launch {
            try {
                action()
            } catch (thrown: Exception) {
                errorLogAndAlert(thrown)
            } finally {
                disableNodes.forEach { it.disabled = false }
                stage.scene.cursor = Cursor.DEFAULT
            }
        }
    }

}