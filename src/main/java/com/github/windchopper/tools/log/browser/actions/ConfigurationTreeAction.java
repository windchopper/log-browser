package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.MainStageController;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.scene.control.TreeView;
import org.jboss.weld.literal.NamedLiteral;

import javax.enterprise.inject.spi.CDI;

public abstract class ConfigurationTreeAction extends AppAction {

    TreeView<ConfigurationNode> view;
    MainStageController mainStageController = CDI.current()
        .select(MainStageController.class, new NamedLiteral("MainStageController"))
        .get();

    public abstract void prepare();

    public TreeView<ConfigurationNode> getView() {
        throw new UnsupportedOperationException();
    }

    public void setView(TreeView<ConfigurationNode> view) {
        this.view = view;
    }

}
