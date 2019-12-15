package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.cdi.form.Form;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;

@ApplicationScoped @Form(Globals.FXML__DOWNLOAD) @Named("GatheringStageController") public class DownloadTabController extends BaseController {

    @Inject private AsyncRunner asyncRunner;

    @FXML private Label statusLabel;
    @FXML private ListView<Connection> connectionListView;
    @FXML private ListView<String> pathListView;
    @FXML private ListView<String> fileListView;

    private Map<LocalDateTime, String> statusLog = new LinkedHashMap<>();

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> formNamespace) {
        super.afterLoad(form, parameters, formNamespace);
        asyncRunner.runAsync(emptyList(), () -> {
            try {
                newStatus(Globals.bundle.getString("com.github.windchopper.tools.log.browser.download.initializing"));

                sleep(2000);
                newStatus("Doing step #1");

                sleep(2000);
                newStatus("Doing step #2");
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void newStatus(String status) {
        runWithFxThread(() -> {
            statusLog.put(LocalDateTime.now(), status);
            statusLabel.setText(status);
        });
    }

    @FXML public void showLogPressed(ActionEvent event) {
        statusLog.forEach(((instant, status) -> System.out.printf("%1$tF %1$tT: %2$s%n", instant, status)));
    }

}
