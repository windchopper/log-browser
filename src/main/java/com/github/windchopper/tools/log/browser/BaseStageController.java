package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.dialog.OptionDialog;
import com.github.windchopper.common.fx.dialog.OptionDialogModel;
import com.github.windchopper.common.fx.form.StageFormController;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

abstract class BaseStageController extends StageFormController implements FormControllerRoutines {

    private static final Image iconImage = new Image("/com/github/windchopper/tools/log/browser/images/scroll-48.png");

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> formNamespace) {
        super.afterLoad(form, parameters, formNamespace);
        stage.getIcons().add(iconImage);
    }

    private void alert(OptionDialog.Type alertType, String message) {
        runWithFxThread(() -> OptionDialog.showOptionDialog(message, alertType, List.of(OptionDialogModel.Option.OK),
            prepareStageDialogFrame(iconImage, Modality.WINDOW_MODAL, false)));
    }

    void informationAlert(String message) {
        alert(OptionDialog.Type.INFORMATION, message);
    }

    void errorAlert(String message) {
        alert(OptionDialog.Type.ERROR, message);
    }

    void errorLogAndAlert(Exception exception) {
        String errorMessage = ExceptionUtils.getRootCauseMessage(exception);
        logger.log(Level.SEVERE, errorMessage, exception);
        errorAlert(errorMessage);
    }

    private Stage topLevelStage(Stage currentStage) {
        Stage topLevelStageCandidate = Stage.getWindows().stream()
            .filter(Stage.class::isInstance)
            .map(Stage.class::cast)
            .filter(stage -> stage.getOwner() == currentStage)
            .findFirst()
            .orElse(null);

        if (topLevelStageCandidate != null) {
            return topLevelStage(topLevelStageCandidate);
        }

        return currentStage;
    }

}
