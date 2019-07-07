package com.github.windchopper.tools.log.browser.io;

import com.github.windchopper.tools.log.browser.Globals;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped @Named("FileSystemTaskRunner") public class FileSystemTaskRunner {

    private ExecutorService executor;

    @PostConstruct void constructed() {
        Integer maxThreadCount = Optional.ofNullable(Globals.fileSystemMaxThreadCount.get())
            .orElseGet(() -> Runtime.getRuntime().availableProcessors());

        executor = Executors.newFixedThreadPool(maxThreadCount);
        Globals.fileSystemMaxThreadCount.accept(maxThreadCount);
    }

    @PreDestroy void destroying() {
        executor.shutdown();
    }

    public void execute(Runnable command) {
        executor.execute(command);
    }

}
