package com.backbase.api.simulator.prism;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class PrismReloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismReloader.class);

    private final PrismServer prismServer;

    public PrismReloader(PrismServer prismServer) {
        this.prismServer = prismServer;
    }

    @Scheduled(cron = "${backbase.api.simulator.refreshSchedule}")
    public void reload() throws IOException, InterruptedException {
        LOGGER.info("Reloading prism to get latest API specification");
        prismServer.restart();
    }
}
