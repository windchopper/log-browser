package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.application.StageController;
import com.github.windchopper.common.util.Pipeliner;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

import static java.util.Collections.singletonList;

abstract class BaseStageController extends StageController {

    private static final Image iconImage = new Image("/com/github/windchopper/tools/log/browser/images/scroll-48.png");

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(
            Pipeliner.of(stage)
                .add(target -> target::getIcons, singletonList(iconImage))
                .get(),
            fxmlResource,
            parameters,
            fxmlLoaderNamespace);
    }

    @Override protected Alert prepareAlert(Supplier<Alert> constructor) {
        return Pipeliner.of(super.prepareAlert(constructor))
            .accept(alert -> ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(iconImage))
            .get();
    }

    private void alert(Alert.AlertType alertType, String message) {
        Alert alert = prepareAlert(Pipeliner.of(() -> new Alert(alertType, message, ButtonType.OK))
            .set(bean -> bean::initOwner, topLevelStage(stage))
            .set(bean -> bean::initModality, Modality.WINDOW_MODAL));

        runWithFxThread(alert::show);
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

    private void runWithFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
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
