package com.github.windchopper.tools.log.browser

import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> List<T>.toObservableList(): ObservableList<T> = FXCollections.observableList(this)

