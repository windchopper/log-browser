package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.application.StageController;
import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Map;
import java.util.function.Supplier;

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

    Stage topLevelStage() {
        return topLevelStage(stage);
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
