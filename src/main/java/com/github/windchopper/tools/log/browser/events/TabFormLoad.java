package com.github.windchopper.tools.log.browser.events;

import com.github.windchopper.common.fx.cdi.form.FormLoad;
import com.github.windchopper.common.util.Resource;
import javafx.collections.ObservableMap;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

public class TabFormLoad extends FormLoad {

    private final Supplier<Tab> tabSupplier;

    public TabFormLoad(Resource resource, Supplier<Tab> tabSupplier) {
        this(resource, emptyMap(), tabSupplier);
    }

    public TabFormLoad(Resource resource, Map<String, ?> parameters, Supplier<Tab> tabSupplier) {
        super(resource, parameters);
        this.tabSupplier = tabSupplier;
    }

    @Override public void afterLoad(Parent form, Object controller, Map<String, ?> parameters, ObservableMap<String, ?> formNamespace) {
        tabSupplier.get().setContent(form);
        super.afterLoad(form, controller, parameters, formNamespace);
    }

}
