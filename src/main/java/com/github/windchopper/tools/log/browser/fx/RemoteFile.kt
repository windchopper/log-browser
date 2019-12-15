package com.github.windchopper.tools.log.browser.fx

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.ListView
import javafx.scene.control.TreeView
import org.apache.commons.lang3.StringUtils
import java.nio.file.Files
import java.nio.file.Path

@Suppress("UNUSED_PARAMETER", "UNUSED_ANONYMOUS_PARAMETER") class RemoteFile(val path: Path): Comparable<RemoteFile> {

    val directory: Boolean
        get() = Files.isDirectory(path)

    override fun compareTo(other: RemoteFile): Int {
        return path.compareTo(other.path)
    }

    fun displayName(appendDirectoryNameWithSlash: Boolean): String {
        var displayName = StringUtils.defaultString(StringUtils.trimToNull(StringUtils.substringAfterLast(path.toString(), "/")), "/")
        if (directory && appendDirectoryNameWithSlash) {
            displayName += "/"
        }
        return displayName
    }

    fun createSelectedProperty(selectedStateBuffer: MutableMap<String, BooleanProperty>, treeView: TreeView<*>, listView: ListView<*>): BooleanProperty {
        val selectedProperty = selectedStateBuffer.computeIfAbsent(path.toString()) { SimpleBooleanProperty(this, "selected") }
        selectedProperty.addListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
            treeView.refresh()
            listView.refresh()
        }
        return selectedProperty
    }

}