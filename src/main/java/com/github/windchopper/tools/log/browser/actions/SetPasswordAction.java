package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.fx.event.FXMLResourceOpen;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.Forms;
import com.github.windchopper.tools.log.browser.MainStageController;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jboss.weld.literal.NamedLiteral;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;

import static java.util.Collections.emptyMap;

@ApplicationScoped @Named("ChangePasswordAction") public class SetPasswordAction extends AppAction {

    public SetPasswordAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.setPassword"));
        setHandler(event -> {
            Platform.runLater(() -> CDI.current().getBeanManager().fireEvent(
                new FXMLResourceOpen(
                    Pipeliner.of(Stage::new)
                        .set(target -> target::initOwner, CDI.current()
                            .select(MainStageController.class, new NamedLiteral("MainStageController"))
                            .get()
                            .getStage())
                        .set(target -> target::initModality, Modality.APPLICATION_MODAL)
                        .set(target -> target::setResizable, false)
                        .get(),
                    Forms.FXML__PASSWORD,
                    emptyMap())));
        });
    }

}
