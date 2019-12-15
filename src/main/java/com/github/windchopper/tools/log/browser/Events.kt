package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.util.Resource
import javafx.collections.ObservableMap
import javafx.scene.Parent
import javafx.scene.control.Tab
import java.util.function.Supplier

class ConfigurationSave

class PathListConfirm(val paths: List<String>)

class TabFormLoad(val resource: Resource, val parameters: Map<String?, *>, private val tabSupplier: Supplier<Tab>): FormLoad(resource, parameters) {

    override fun afterLoad(form: Parent, controller: Any, parameters: Map<String?, *>, formNamespace: ObservableMap<String?, *>) {
        tabSupplier.get().content = form
        super.afterLoad(form, controller, parameters, formNamespace)
    }

}