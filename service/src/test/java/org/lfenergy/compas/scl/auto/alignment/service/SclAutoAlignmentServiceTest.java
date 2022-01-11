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
import java.util.List;

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
    void updateSCL_WhenPassingCase1_ThenUpdatedSCLReturned() throws IOException {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.updateSCL(sclString, List.of("AA1"), "Mr. Editor");
        assertNotNull(result);
        writeFile(filename + "-updated-service.scd", result);
    }

    @Test
    void updateSCL_WhenPassingCase2_ThenUpdatedSCLReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.updateSCL(sclString, List.of("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4"), "Mr. Editor");
        assertNotNull(result);
        writeFile(filename + "-updated-service.scd", result);
    }

    @Test
    void getSVG_WhenPassingCase1_ThenSVGReturned() throws IOException {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "AA1");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }

    @Test
    void getSVG_WhenPassingCase2_ThenSVGReturned() throws IOException {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }

    @Test
    void createJson_WhenPassingCase1_ThenJsonReturned() throws IOException {
        var filename = "scl-1";
        var substationName = "AA1";
        var sclData = readSCL(filename + ".scd");
        var scl = sclAutoAlignmentService.readSCL(sclData);
        var substationBuilder = sclAutoAlignmentService.createSubstationGraphBuilder(scl, substationName);

        var result = sclAutoAlignmentService.createJson(substationBuilder);
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }

    @Test
    void createJson_WhenPassingCase2_ThenJsonReturned() throws IOException {
        var filename = "scl-2";
        var substationName = "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4";
        var sclData = readSCL(filename + ".scd");
        var scl = sclAutoAlignmentService.readSCL(sclData);
        var substationBuilder = sclAutoAlignmentService.createSubstationGraphBuilder(scl, substationName);

        var result = sclAutoAlignmentService.createJson(substationBuilder);
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }
}