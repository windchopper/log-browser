package com.github.windchopper.tools.log.browser.events;

import com.github.windchopper.common.fx.form.FormLoad;
import javafx.collections.ObservableMap;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.util.Map;

public class TabFormLoad extends FormLoad {

    private Tab tab;

    public TabFormLoad(Tab tab, String resource) {
        super(resource);
        this.tab = tab;
    }

    public TabFormLoad(Tab tab, String resource, Map<String, ?> parameters) {
        super(resource, parameters);
        this.tab = tab;
    }

    @Override
    public void afterLoad(Parent form, Object controller, Map<String, ?> parameters, ObservableMap<String, ?> formNamespace) {
        tab.setContent(form);
        super.afterLoad(form, controller, parameters, formNamespace);
    }

}
