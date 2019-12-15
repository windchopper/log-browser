package com.github.windchopper.tools.log.browser

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.MenuItem

fun <T> List<T>.toObservableList(): ObservableList<T> = FXCollections.observableList(this)

fun Node.generalize(): NodeOrMenuItem = object: NodeOrMenuItem {
    override var disabled: Boolean
        get() = this@generalize.isDisable
        set(value) {
            this@generalize.isDisable = value
        }
}

fun MenuItem.generalize(): NodeOrMenuItem = object: NodeOrMenuItem {
    override var disabled: Boolean
        get() = this@generalize.isDisable
        set(value) {
            this@generalize.isDisable = value
        }
}