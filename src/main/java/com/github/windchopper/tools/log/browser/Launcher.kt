package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad
import com.github.windchopper.common.fx.cdi.form.FormLoader
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.ClassPathResource
import javafx.application.Application
import javafx.stage.Stage
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer

class Launcher: Application() {

    private var weld: Weld = Weld()
        .enableDiscovery()
        .addPackages(FormLoader::class.java)
    private var weldContainer: WeldContainer = weld.initialize()

    override fun stop() {
        weld.shutdown()
        super.stop()
    }

    override fun start(primaryStage: Stage) {
        with (weldContainer.beanManager) {
            fireEvent(ResourceBundleLoad(Globals.bundle))
            fireEvent(StageFormLoad(ClassPathResource(Globals.FXML__MAIN)) { primaryStage })
        }
    }

}

fun main(vararg args: String) {
    Application.launch(*args)
}
