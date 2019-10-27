package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.cdi.form.Form;
import com.github.windchopper.tools.log.browser.configuration.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Map;

@ApplicationScoped @Form(Globals.FXML__GROUP) @Named("GroupStageController") public class GroupStageController extends BaseStageController {

    @FXML private TextField nameField;

    private Group group;

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.afterLoad(form, parameters, fxmlLoaderNamespace);

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
