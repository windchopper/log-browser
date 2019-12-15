package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.tools.log.browser.configuration.Group
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextField
import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Form(Globals.FXML__GROUP) @Named("GroupStageController") @Suppress("UNUSED_PARAMETER") class GroupStageController: BaseStageController() {

    @FXML private lateinit var nameField: TextField
    private var group: Group? = null

    override fun afterLoad(form: Parent, parameters: Map<String?, *>, formNamespace: Map<String?, *>) {
        super.afterLoad(form, parameters, formNamespace)
        group = (parameters["group"] as Group).also {
            nameField.text = it.name
        }
    }

    @FXML fun saveSelected(event: ActionEvent) {
        group!!.name = nameField.text
        stage.close()
    }

    @FXML fun cancelSelected(event: ActionEvent) {
        stage.close()
    }

}