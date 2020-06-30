package com.backbase.api.simulator.util;

import java.nio.file.Path;

public class Paths {

    private Paths() {
    }

    public static String getSpecPath(String spec, int serverPort) {
        return spec.startsWith("http") ? "http://localhost:" + serverPort + "/simulator/openapi.yaml"
            : getAbsolutePath(java.nio.file.Paths.get(spec));
    }

    public static String getAbsolutePath(Path path) {
        return path.normalize().toAbsolutePath().toString();
    }
}
