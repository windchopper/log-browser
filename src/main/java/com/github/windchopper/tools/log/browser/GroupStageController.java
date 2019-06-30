package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.tools.log.browser.configuration.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Map;

@ApplicationScoped @FXMLResource(Globals.FXML__GROUP) @Named("GroupStageController") public class GroupStageController extends BaseStageController {

    @FXML private TextField nameField;

    private Group group;

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        group = (Group) parameters.get("group");

        if (group != null) {
            nameField.setText(group.getName());
        }
    }

    @FXML public void saveSelected(ActionEvent event) {
        group.setName(nameField.getText());
        stage.close();
    }

    @FXML public void cancelSelected(ActionEvent event) {
        stage.close();
    }

}
