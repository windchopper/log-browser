package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.form.Form
import jakarta.enterprise.context.Dependent
import jakarta.inject.Named
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

@Dependent @Form(Globals.FXML__DOWNLOAD) @Named("GatheringStageController") @Suppress("UNUSED_PARAMETER") class DownloadTabController: BaseController() {

    @FXML private lateinit var progressBar: ProgressBar
    @FXML private lateinit var statusLabel: Label
    @FXML private lateinit var connectionListView: ListView<Connection>
    @FXML private lateinit var pathListView: ListView<String>
    @FXML private lateinit var fileListView: ListView<String>

    private val statusLog: MutableMap<LocalDateTime, String> = LinkedHashMap()

    override fun afterLoad(form: Parent, parameters: Map<String?, *>?, formNamespace: Map<String?, *>?) {
        super.afterLoad(form, parameters, formNamespace)

        GlobalScope.launch {
            newStatus(Globals.bundle.getString("com.github.windchopper.tools.log.browser.download.initializing"))
            delay(2000)
            newStatus("Doing step #1")
            delay(2000)
            newStatus("Doing step #2")
            delay(2000)
            newStatus("Done")
            launch (Dispatchers.JavaFx) {
                progressBar.progress = 100.0
            }
        }
    }

    fun newStatus(status: String) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            statusLog[LocalDateTime.now()] = status
            statusLabel.text = status
        }
    }

    @FXML fun showLogPressed(event: ActionEvent) {
        statusLog.forEach { (instant, status) ->
            System.out.printf("%1\$tF %1\$tT: %2\$s%n", instant, status)
        }
    }

}