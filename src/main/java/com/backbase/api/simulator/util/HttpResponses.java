package com.backbase.api.simulator.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.servlet.http.HttpServletResponse;

public class HttpResponses {

    private HttpResponses() {
    }

    /**
     * Writes content to an HTTP response.
     *
     * @param content  Content to write.
     * @param response HTTP response to write to.
     * @throws IOException If an I/O error occurs while writing the content.
     */
    public static void writeResponse(String content, HttpServletResponse response) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.write(content);
        }
    }
}
