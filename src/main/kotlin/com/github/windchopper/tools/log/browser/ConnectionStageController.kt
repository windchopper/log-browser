package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.CellFactories
import com.github.windchopper.common.fx.DelegatingStringConverter
import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.fx.spinner.FlexibleSpinnerValueFactory
import com.github.windchopper.common.fx.spinner.NumberType
import com.github.windchopper.common.util.ClassPathResource
import jakarta.enterprise.context.Dependent
import jakarta.enterprise.event.Event
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.inject.Named
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException
import java.nio.file.FileSystem

@Dependent
@Form(Globals.FXML__CONNECTION) @Named("ConnectionStageController") @Suppress("UNUSED_PARAMETER") class ConnectionStageController: BaseStageController() {

    @Inject private lateinit var fxmlResourceOpenEvent: Event<FormLoad>
    @Inject private lateinit var saveConfigurationEvent: Event<ConfigurationSave>

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var typeBox: ComboBox<ConnectionType?>
    @FXML private lateinit var hostField: TextField
    @FXML private lateinit var portSpinner: Spinner<Number?>
    @FXML private lateinit var usernameField: TextField
    @FXML private lateinit var passwordField: PasswordField
    @FXML private lateinit var pathListView: ListView<String>
    @FXML private lateinit var choosePathListButton: Button

    private var connection: Connection? = null

    override fun afterLoad(form: Parent, parameters: Map<String?, *>, formNamespace: Map<String?, *>) {
        super.afterLoad(form, parameters, formNamespace)
        stage.isResizable = false
        typeBox.items.addAll(*ConnectionType.values())
        typeBox.converter = DelegatingStringConverter { it?.displayName }
        typeBox.cellFactory = CellFactories.listCellFactory { cell: ListCell<ConnectionType?>, item: ConnectionType?, empty: Boolean -> cell.setText(if (empty || item == null) null else item.displayName) }
        portSpinner.valueFactory = FlexibleSpinnerValueFactory(NumberType.INTEGER, 0, 65535, 0)
        connection = (parameters["connection"] as Connection).also {
            nameField.text = it.name
            typeBox.value = it.type
            hostField.text = it.host
            portSpinner.valueFactory.value = it.port?:0
            usernameField.text = it.username
            passwordField.text = it.password
            pathListView.items = connection?.pathList?.toObservableList()
        }
    }

    @Throws(IOException::class) private fun newFileSystem(): FileSystem {
        val connectionType = typeBox.value
            ?:throw IllegalStateException(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.type.notSelected"))
        return connectionType.newFileSystem(
            hostField.text,
            portSpinner.value!!.toInt(),
            usernameField.text,
            passwordField.text)
    }

    fun pathListConfirmed(@Observes confirmPathList: PathListConfirm) {
        pathListView.items = confirmPathList.paths.toObservableList()
    }

    @FXML fun typeSelected(event: ActionEvent?) {
        portSpinner.valueFactory.value = typeBox.value!!.defaultPort
    }

    @FXML fun savePressed(event: ActionEvent) {
        connection?.let {
            with (it) {
                name = nameField.text
                type = typeBox.value
                host = hostField.text
                port = portSpinner.value!!.toInt()
                username = usernameField.text
                password = passwordField.text
                pathList = pathListView.items.toMutableList()
            }
        }

        saveConfigurationEvent.fire(ConfigurationSave())
        stage.close()
    }

    @FXML fun choosePathListButton(event: ActionEvent) {
        stage.blockingAction(choosePathListButton) {
            fxmlResourceOpenEvent.fire(
                StageFormLoad(ClassPathResource(Globals.FXML__BROWSE), mapOf("fileSystem" to newFileSystem())) {
                    Stage().also {
                        it.initOwner(stage)
                        it.initModality(Modality.WINDOW_MODAL)
                    }
                })
        }
    }

}