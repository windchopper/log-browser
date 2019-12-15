package com.github.windchopper.tools.log.browser

import javafx.beans.property.BooleanProperty
import javafx.scene.Cursor
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Named("AsyncRunner") class AsyncRunner {

    fun runAsyncWithBusyPointer(stage: Stage, actionDisableProperties: Collection<BooleanProperty>, action: Runnable) {
        GlobalScope.launch {
            stage.scene.cursor = Cursor.WAIT

            actionDisableProperties.forEach {
                it.set(true)
            }

            try {
                action.run()
            } finally {
                stage.scene.cursor = Cursor.DEFAULT

                actionDisableProperties.forEach{
                    it.set(false)
                }
            }
        }
    }

    fun runAsync(actionDisableProperties: Collection<BooleanProperty>, action: Runnable) {
        GlobalScope.launch {
            actionDisableProperties.forEach {
                it.set(true)
            }

            try {
                action.run()
            } finally {
                actionDisableProperties.forEach {
                    it.set(false)
                }
            }
        }
    }

}