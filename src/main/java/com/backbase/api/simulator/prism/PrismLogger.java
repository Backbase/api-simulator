package com.backbase.api.simulator.prism;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrismLogger implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismLogger.class);

    private final String prefix;
    private final InputStream inputStream;

    public PrismLogger(String prefix, InputStream inputStream) {
        this.prefix = prefix;
        this.inputStream = inputStream;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("{}: {}", prefix, line);
            }
        } catch (InterruptedIOException e) {
            // Nothing to do
        } catch (IOException e) {
            LOGGER.error("Couldn't read output from prism server", e);
        }
    }
}
