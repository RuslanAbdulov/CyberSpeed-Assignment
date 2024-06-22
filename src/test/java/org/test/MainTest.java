package org.test;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMain() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        String[] args = {"--config", "src/test/resources/test-config.json", "--betting-amount", "100"};
        Main.main(args);

        var output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("matrix"));
    }

}