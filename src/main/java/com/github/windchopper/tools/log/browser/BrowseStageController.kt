package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormController
import com.github.windchopper.tools.log.browser.events.PathListConfirm
import com.github.windchopper.tools.log.browser.fx.FileListCell
import com.github.windchopper.tools.log.browser.fx.FileTreeCell
import com.github.windchopper.tools.log.browser.fx.RemoteFile
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.stage.WindowEvent
import javafx.util.Callback
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.IOException
import java.nio.file.FileSystem
import java.nio.file.Files
import java.util.*
import java.util.logging.Level
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.inject.Inject
import javax.inject.Named

@ApplicationScoped @Form(Globals.FXML__BROWSE) @Named("BrowseStageController") @Suppress("UNUSED_PARAMETER") class BrowseStageController: BaseStageController() {

    @Inject private lateinit var confirmPathListEvent: Event<PathListConfirm>
    @FXML private lateinit var pathField: TextField
    @FXML private lateinit var directoryTreeView: TreeView<RemoteFile>
    @FXML private lateinit var fileListView: ListView<RemoteFile>

    private var fileSystem: FileSystem? = null
    private val directoryTreeRoot = TreeItem<RemoteFile>()
    private val selectedStateBuffer: MutableMap<String, BooleanProperty> = HashMap()

    fun loadTree(selectedFile: RemoteFile, directoryItems: MutableList<TreeItem<RemoteFile?>?>, files: MutableList<RemoteFile?>): Boolean {
        return try {
            Files.list(selectedFile.path)
                .sorted()
                .map { RemoteFile(it) }
                .peek { files.add(it) }
                .filter { it.directory }
                .map { TreeItem(it) }
                .forEach { directoryItems.add(it) }
            true
        } catch (thrown: IOException) {
            errorLogAndAlert(thrown)
            false
        }
    }

    override fun afterLoad(form: Parent, parameters: Map<String?, *>, formNamespace: Map<String?, *>) {
        super.afterLoad(form, parameters, formNamespace)
        stage.onCloseRequest = EventHandler { event: WindowEvent? -> closeFileSystem() }
        fileSystem = parameters["fileSystem"] as FileSystem?
        directoryTreeView.selectionModel.selectionMode = SelectionMode.SINGLE
        directoryTreeView.setCellFactory { view: TreeView<RemoteFile> ->
            FileTreeCell(
                { item: TreeItem<RemoteFile> -> item.value.createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView) },
                selectedStateBuffer)
        }
        directoryTreeView.selectionModel.selectedItemProperty().addListener { observable: ObservableValue<out TreeItem<RemoteFile>?>?, unselectedItem: TreeItem<RemoteFile>?, selectedItem: TreeItem<RemoteFile>? ->
            if (fileSystem == null || selectedItem == null) {
                return@addListener
            }
            val selectedFile = selectedItem.value
            pathField.text = selectedFile.path.toString()
            selectedItem.children.clear()
            fileListView.items.clear()
            launchWithWaitCursor(pathField.generalize(), directoryTreeView.generalize(), fileListView.generalize()) {
                val directoryItems: MutableList<TreeItem<RemoteFile?>?> = ArrayList()
                val files: MutableList<RemoteFile?> = ArrayList()
                if (loadTree(selectedFile, directoryItems, files)) {
                    Platform.runLater {
                        fileListView.items.addAll(files)
                        selectedItem.children.addAll(directoryItems)
                        selectedItem.setExpanded(true)
                    }
                }
            }
        }
        fileListView.selectionModel.selectionMode = SelectionMode.SINGLE
        fileListView.setCellFactory { view: ListView<RemoteFile>? ->
            FileListCell(
                Callback { file: RemoteFile -> file.createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView) },
                selectedStateBuffer)
        }
        fileListView.onMouseClicked = EventHandler { event: MouseEvent ->
            val selectedFile = fileListView.selectionModel.selectedItem
            if (selectedFile != null && selectedFile.directory && event.clickCount > 1) {
                directoryTreeView.selectionModel.clearSelection()
                fileListView.items.clear()
                val path = selectedFile.path.normalize().toString()
                pathField.text = path
                val treeItem = findTreeItemByPath(directoryTreeView.root, path)
                treeItem?.children?.clear()
                launchWithWaitCursor(pathField.generalize(), directoryTreeView.generalize(), fileListView.generalize()) {
                    val directoryItems: MutableList<TreeItem<RemoteFile?>?> = ArrayList()
                    val files: MutableList<RemoteFile?> = ArrayList()
                    if (loadTree(selectedFile, directoryItems, files)) {
                        Platform.runLater {
                            fileListView.items.addAll(files)
                            if (treeItem != null) {
                                treeItem.children.addAll(directoryItems)
                                var item = treeItem
                                while (item != null) {
                                    item.isExpanded = true
                                    item = item.parent
                                }
                                directoryTreeView.scrollTo(directoryTreeView.getRow(treeItem))
                                directoryTreeView.selectionModel.select(treeItem)
                            }
                        }
                    }
                }
            }
        }
        directoryTreeView.setRoot(directoryTreeRoot)
        fileSystem!!.rootDirectories
            .sorted()
            .map { RemoteFile(it) }
            .map { TreeItem(it) }
            .forEach { directoryTreeRoot.children.add(it) }
    }

    @PreDestroy fun closeFileSystem() {
        if (fileSystem != null) {
            try {
                fileSystem!!.close()
            } catch (thrown: IOException) {
                FormController.logger.log(Level.SEVERE, ExceptionUtils.getRootCauseMessage(thrown), thrown)
            }
        }
    }

    private fun findTreeItemByPath(item: TreeItem<RemoteFile>, path: String): TreeItem<RemoteFile>? {
        if (StringUtils.equals(item.value.path.toString(), path)) {
            return item
        }
        for (childItem in item.children) {
            if (StringUtils.startsWith(path, childItem.value.path.toString())) {
                return findTreeItemByPath(childItem, path)
            }
        }
        return null
    }

    @FXML fun confirmPressed(event: ActionEvent) {
        stage.close()
        confirmPathListEvent.fire(PathListConfirm(selectedStateBuffer.entries
            .filter { it.value.get() }
            .map { it.key }))
    }

}