package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.CellFactories
import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.Builder
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.common.util.Pipeliner
import com.github.windchopper.common.util.Resource
import com.github.windchopper.tools.log.browser.Globals.bundle
import com.github.windchopper.tools.log.browser.configuration.Configuration
import com.github.windchopper.tools.log.browser.configuration.ConfigurationElement
import com.github.windchopper.tools.log.browser.configuration.Connection
import com.github.windchopper.tools.log.browser.configuration.Group
import com.github.windchopper.tools.log.browser.events.ConfigurationSave
import com.github.windchopper.tools.log.browser.events.TabFormLoad
import com.github.windchopper.tools.log.browser.util.generalize
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.inject.Named

@ApplicationScoped @Form(Globals.FXML__MAIN) @Named("MainStageController") @Suppress("UNUSED_PARAMETER") class MainStageController: BaseStageController() {

    @Inject private lateinit var formLoadEvent: Event<FormLoad>
    @Inject private lateinit var configurationSaveEvent: Event<ConfigurationSave>
    @Inject private lateinit var configurationAccess: ConfigurationAccess

    @FXML private lateinit var configurationTreeView: TreeView<ConfigurationElement>
    @FXML private lateinit var configurationTreeRoot: TreeItem<ConfigurationElement>
    @FXML private lateinit var configurationButton: MenuButton
    @FXML private lateinit var importConfigurationMenuItem: MenuItem
    @FXML private lateinit var exportConfigurationMenuItem: MenuItem
    @FXML private lateinit var downloadMenuItem: MenuItem
    @FXML private lateinit var addGroupMenuItem: MenuItem
    @FXML private lateinit var addConnectionMenuItem: MenuItem
    @FXML private lateinit var removeMenuItem: MenuItem
    @FXML private lateinit var groupMenuItem: MenuItem
    @FXML private lateinit var ungroupMenuItem: MenuItem
    @FXML private lateinit var propertiesMenuItem: MenuItem
    @FXML private lateinit var workareaPane: TabPane

    override fun afterLoad(form: Parent, parameters: Map<String?, *>, formNamespace: Map<String?, *>) {
        super.afterLoad(form, parameters, formNamespace)
        stage.title = bundle.getString("com.github.windchopper.tools.log.browser.main.title")
        loadWithConfigurationNode(configurationTreeRoot, configurationAccess.configuration)
        configurationTreeView.selectionModel.selectionMode = SelectionMode.MULTIPLE
        configurationTreeView.cellFactory = CellFactories.treeCellFactory { cell: TreeCell<ConfigurationElement>, item: ConfigurationElement?, empty: Boolean -> cell.setText(if (empty || item == null) null else item.name) }
        configurationTreeView.onEditCommit = EventHandler { editEvent: TreeView.EditEvent<ConfigurationElement>? -> configurationSaveEvent.fire(ConfigurationSave()) }
    }

    private fun <T: ConfigurationElement?> loadWithConfigurationNode(item: TreeItem<ConfigurationElement>?, configurationNode: T) {
        item!!.value = configurationNode
        if (configurationNode is Group) {
            with (configurationNode as Group) {
                groups.forEach {
                    loadWithConfigurationNode(
                        TreeItem<ConfigurationElement>().also {
                            item.children.add(it)
                        },
                        it)
                }
                connections.forEach {
                    loadWithConfigurationNode(
                        TreeItem<ConfigurationElement>().also {
                            item.children.add(it)
                        },
                        it)
                }
            }
        }
    }

    fun saveConfiguration(@Observes saveConfiguration: ConfigurationSave) {
        GlobalScope.launch {
            configurationAccess.saveConfiguration()
            configurationTreeView.refresh()
        }
    }

    @FXML fun contextMenuShowing(event: WindowEvent?) {
        val selectedItems = configurationTreeView.selectionModel.selectedItems
        addGroupMenuItem.isDisable = 1L != selectedItems.stream()
            .map { obj: TreeItem<ConfigurationElement> -> obj.value }
            .filter(Predicate.not { item: ConfigurationElement? -> item is Connection })
            .count()
        addConnectionMenuItem.isDisable = 1 != selectedItems.size
        removeMenuItem.isDisable = selectedItems.isEmpty() || selectedItems.stream()
            .map { obj: TreeItem<ConfigurationElement> -> obj.value }
            .anyMatch { item: ConfigurationElement? -> item is Configuration }
        propertiesMenuItem.isDisable = 1L != selectedItems.stream()
            .map { obj: TreeItem<ConfigurationElement> -> obj.value }
            .filter(Predicate.not { item: ConfigurationElement? -> item is Configuration })
            .count()
    }

    private fun openWindow(fxmlResource: Resource, parameterName: String, parameter: Any) {
        launchWithWaitCursor(propertiesMenuItem.generalize()) {
            formLoadEvent.fire(
                StageFormLoad(fxmlResource, mapOf(parameterName to parameter)) {
                    Stage().also {
                        it.initOwner(stage)
                        it.initModality(Modality.WINDOW_MODAL)
                    }
                })
        }
    }

    private fun openConnectionWindow(connection: Connection) {
        openWindow(ClassPathResource(Globals.FXML__CONNECTION), "connection", connection)
    }

    private fun openGroupWindow(group: Group) {
        openWindow(ClassPathResource(Globals.FXML__GROUP), "group", group)
    }

    @FXML fun downloadSelected(event: ActionEvent?) {
        formLoadEvent.fire(TabFormLoad(
            ClassPathResource(Globals.FXML__DOWNLOAD),
            java.util.Map.of("selection", java.util.List.copyOf(configurationTreeView.selectionModel.selectedItems)),
            Pipeliner.of { Tab() }
                .set({ tab: Tab -> Consumer { value: String? -> tab.text = value } }, String.format("%1\$tF %1\$tT", LocalDateTime.now()))
                .accept { tab: Tab? -> workareaPane.tabs.add(tab) }
                .accept { tab: Tab? -> workareaPane.selectionModel.select(tab) }))
    }

    @FXML fun addGroupSelected(event: ActionEvent?) {
        val selectionModel = configurationTreeView.selectionModel
        val parentItem = selectionModel.selectedItem
        if (parentItem.value is Group) {
            val group = (parentItem.value as Group).addGroup()
            group.name = bundle.getString("com.github.windchopper.tools.log.browser.main.newGroup")
            val connectionItem = TreeItem<ConfigurationElement>()
            connectionItem.value = group
            val itemContainer = parentItem.children
            var targetIndex = itemContainer.size
            var i = 0
            val count = itemContainer.size
            while (i < count) {
                if (itemContainer[i].value is Connection) {
                    targetIndex = i
                    break
                }
                i++
            }
            itemContainer.add(targetIndex, connectionItem)
            parentItem.isExpanded = true
            selectionModel.clearSelection()
            selectionModel.select(connectionItem)
            configurationSaveEvent.fire(ConfigurationSave())
            openGroupWindow(group)
        }
    }

    @FXML fun addConnectionSelected(event: ActionEvent?) {
        val selectionModel = configurationTreeView.selectionModel
        val parentItem = selectionModel.selectedItem
        if (parentItem.value is Group) {
            val connection = (parentItem.value as Group).addConnection()
            connection.name = bundle.getString("com.github.windchopper.tools.log.browser.main.newConnection")
            val connectionItem = TreeItem<ConfigurationElement>()
            connectionItem.value = connection
            parentItem.children.add(connectionItem)
            parentItem.isExpanded = true
            selectionModel.clearSelection()
            selectionModel.select(connectionItem)
            configurationSaveEvent.fire(ConfigurationSave())
            openConnectionWindow(connection)
        }
    }

    @FXML fun propertiesSelected(event: ActionEvent?) {
        val selectedItem = configurationTreeView.selectionModel.selectedItem
        if (selectedItem.value is Connection) {
            openConnectionWindow(selectedItem.value as Connection)
        } else if (selectedItem.value is Group) {
            openGroupWindow(selectedItem.value as Group)
        }
    }

    @FXML fun removeSelected(event: ActionEvent?) {
        val selectedItems: MutableList<TreeItem<ConfigurationElement>> = ArrayList(configurationTreeView.selectionModel.selectedItems)
        configurationTreeView.selectionModel.clearSelection()
        while (selectedItems.size > 0) {
            val copyOfSelectedItems: List<TreeItem<ConfigurationElement>> = ArrayList(selectedItems)
            if (!selectedItems.removeIf { item: TreeItem<ConfigurationElement> -> item.parent == null || copyOfSelectedItems.contains(item.parent) }) {
                break
            }
        }
        for (item in selectedItems) {
            val parentItem = item.parent
            val parentGroup = parentItem.value as Group
            val configurationElement = item.value
            parentItem.children.remove(item)
            if (configurationElement is Connection) {
                parentGroup.connections.remove(configurationElement)
            } else if (configurationElement is Group) {
                parentGroup.groups.remove(configurationElement)
            }
        }
        configurationSaveEvent.fire(ConfigurationSave())
    }

    @FXML fun groupSelected(event: ActionEvent) {
    }

    @FXML fun ungroupSelected(event: ActionEvent) {
    }

}