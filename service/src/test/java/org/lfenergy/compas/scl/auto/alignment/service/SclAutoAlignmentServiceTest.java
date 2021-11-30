// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Disabled
@ExtendWith(MockitoExtension.class)
class SclAutoAlignmentServiceTest {

    private SclAutoAlignmentService sclAutoAlignmentService;

    private final ElementConverter converter = new ElementConverter();

    @BeforeEach
    void beforeEach() {
        sclAutoAlignmentService = new SclAutoAlignmentService(converter);
    }

    @Test
    void getJson_WhenPassingCase1_ThenJsonReturned() throws IOException {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        writeFile(filename + ".json", sclAutoAlignmentService.getJson(sclString, "AA1"));
    }

    @Test
    void getJson_WhenPassingCase2_ThenJsonReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        writeFile(filename + ".json", sclAutoAlignmentService.getJson(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4"));
    }

    @Test
    void getSVG_WhenPassingCase1_ThenJsonReturned() throws IOException {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        writeFile(filename + ".svg", sclAutoAlignmentService.getSVG(sclString, "AA1"));
    }

    @Test
    void getSVG_WhenPassingCase2_ThenJsonReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        writeFile(filename + ".svg", sclAutoAlignmentService.getSVG(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4"));
    }

    private void writeFile(String fileName, String data) {
        File file = new File("target", fileName);
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            fw.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private String readSCL(String filename) throws IOException {
        var inputStream = getClass().getResourceAsStream("/scl/" + filename);
        assert inputStream != null;

        return new String(inputStream.readAllBytes());
    }
}