package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.form.StageFormController;
import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

abstract class BaseStageController extends StageFormController implements FormControllerRoutines {

    private static final Image iconImage = new Image("/com/github/windchopper/tools/log/browser/images/scroll-48.png");

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> formNamespace) {
        super.afterLoad(form, parameters, formNamespace);
        stage.getIcons().add(iconImage);
    }

    @Override protected Alert prepareAlert(Supplier<Alert> constructor) {
        return Pipeliner.of(super.prepareAlert(constructor))
            .accept(alert -> ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(iconImage))
            .get();
    }

    private void alert(Alert.AlertType alertType, String message) {
        runWithFxThread(() -> prepareAlert(Pipeliner.of(() -> new Alert(alertType, message, ButtonType.OK))
            .set(bean -> bean::initOwner, topLevelStage(stage))
            .set(bean -> bean::initModality, Modality.WINDOW_MODAL))
            .show());
    }

    void informationAlert(String message) {
        alert(Alert.AlertType.INFORMATION, message);
    }

    @SuppressWarnings("WeakerAccess") void errorAlert(String message) {
        alert(Alert.AlertType.ERROR, message);
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
