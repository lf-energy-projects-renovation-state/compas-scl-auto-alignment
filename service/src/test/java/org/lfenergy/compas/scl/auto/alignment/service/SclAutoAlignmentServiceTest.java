// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCL;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.writeFile;

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

        var result = sclAutoAlignmentService.getJson(sclString, "AA1");
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }

    @Test
    void getJson_WhenPassingCase2_ThenJsonReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getJson(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4");
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }

    @Test
    void getSVG_WhenPassingCase1_ThenJsonReturned() throws IOException {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "AA1");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }

    @Test
    void getSVG_WhenPassingCase2_ThenJsonReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }
}