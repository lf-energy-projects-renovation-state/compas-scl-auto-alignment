// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;

class SclAutoAlignmentEnricherTest {
    private final ElementConverter converter = new ElementConverter();

    @Test
    void enrich_WhenPassingCase1_ThenXSDUpdated() throws IOException {
        var filename = "scl-1";
        var jsonString = readFile("/json/" + filename + ".json");
        var scl = readSCL("/scl/" + filename + ".scd");

        var sclAutoAlignmentEnricher = new SclAutoAlignmentEnricher(scl, jsonString);
        sclAutoAlignmentEnricher.enrich();

        assertNotNull(scl.getElement());
        writeFile(filename + "-updated.scd", scl);
    }

    @Test
    void enrich_WhenPassingCase2_ThenXSDUpdated() throws IOException {
        var filename = "scl-2";
        var jsonString = readFile("/json/" + filename + ".json");
        var scl = readSCL("/scl/" + filename + ".scd");

        var sclAutoAlignmentEnricher = new SclAutoAlignmentEnricher(scl, jsonString);
        sclAutoAlignmentEnricher.enrich();

        assertNotNull(scl.getElement());
        writeFile(filename + "-updated.scd", scl);
    }

    private GenericSCL readSCL(String filename) throws IOException {
        var sclData = readFile(filename);
        var sclElement = converter.convertToElement(new BufferedInputStream(
                new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
        return new GenericSCL(sclElement);
    }

    private void writeFile(String fileName, GenericSCL scl) {
        var sclData = converter.convertToString(scl.getElement());
        File file = new File("target", fileName);
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            fw.write(sclData);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readFile(String filename) throws IOException {
        var inputStream = getClass().getResourceAsStream(filename);
        assert inputStream != null;

        return new String(inputStream.readAllBytes());
    }
}