package com.github.windchopper.tools.log.browser.fs;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JSchLoggerBridge implements com.jcraft.jsch.Logger {

    private final Logger logger;

    JSchLoggerBridge(Logger logger) {
        this.logger = logger;
    }

    private Level translateLevel(int level) {
        switch (level) {
            case DEBUG: return Level.FINE;
            case INFO: return Level.INFO;
            case WARN: return Level.WARNING;
            case ERROR: return Level.SEVERE;
            case FATAL: return Level.SEVERE;
            default: return Level.OFF;
        }
    }

    @Override public boolean isEnabled(int level) {
        return logger.isLoggable(translateLevel(level));
    }

    @Override public void log(int level, String message) {
        logger.log(translateLevel(level), message);
    }

}
