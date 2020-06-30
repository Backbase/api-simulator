package com.backbase.api.simulator.prism;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs all Prism output and indicates whether it was started successfully or not.
 */
public class PrismLogger implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismLogger.class);

    private static final String SUCCESS_MESSAGE = "Prism is listening on";
    private static final String ERROR_MESSAGE = "Error:";

    private final PrismServer prismServer;
    private final String prefix;
    private final InputStream inputStream;

    /**
     * Construct a new instance.
     *
     * @param prismServer Prism server to monitor.
     * @param prefix      Prefix to use in logging messages.
     * @param inputStream Prism server console output stream.
     */
    public PrismLogger(PrismServer prismServer, String prefix, InputStream inputStream) {
        this.prismServer = prismServer;
        this.prefix = prefix;
        this.inputStream = inputStream;
    }

    /**
     * Starts monitoring the Prism server.
     */
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("{}: {}", prefix, line);

                if (line.contains(SUCCESS_MESSAGE)) {
                    prismServer.onPrismStartResult(true);
                } else if (line.contains(ERROR_MESSAGE)) {
                    prismServer.onPrismStartResult(false);
                }
            }
        } catch (InterruptedIOException e) {
            // Nothing to do
        } catch (IOException e) {
            LOGGER.error("Couldn't read output from prism server", e);
        }
    }
}
