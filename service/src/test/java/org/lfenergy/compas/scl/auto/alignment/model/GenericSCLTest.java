// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;

class GenericSCLTest {
    public static final String BASIC_SCD_FILENAME = "scl-1.scd";

    private GenericSCL scl;

    @BeforeEach
    void setup() throws IOException {
        scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
    }

    @Test
    void constructor_WhenObjectCreatedAlreadyContainingSXYNamespace_ThenElementSetAndNamespaceStillThere() {
        assertNotNull(scl.getElement());
        assertEquals(SCLXY_NS_URI, scl.getElement().getAttribute("xmlns:" + SCLXY_PREFIX));
    }

    @Test
    void constructor_WhenObjectCreatedAlreadyWithoutSXYNamespace_ThenElementSetAndNamespaceSet() throws IOException {
        scl = new GenericSCL(readSCLElement("scl-2.scd"));

        assertNotNull(scl.getElement());
        assertEquals(SCLXY_NS_URI, scl.getElement().getAttribute("xmlns:" + SCLXY_PREFIX));
    }

    @Test
    void getFullName_WhenCalled_ThenForSCLEmptyStringReturned() {
        var result = scl.getFullName();
        assertEquals("", result);
    }

    @Test
    void getSubstations_WhenCalledOnExampleSCL_ThenSubstationsAreReturned() {
        var result = scl.getSubstations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(SUBSTATION_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, scl.getSubstations());
    }

    @Test
    void getSubstation_WhenCalledOnExampleSCL_ThenSubstationIsReturned() {
        var result = scl.getSubstation(SUBSTATION_NAME);

        assertTrue(result.isPresent());
        assertEquals(SUBSTATION_NAME, result.get().getName());
    }

    @Test
    void getSubstation_WhenCalledWithBlankName_ThenEmptyOptional() {
        var result = scl.getSubstation("");

        assertFalse(result.isPresent());
    }

    @Test
    void getOrCreateHeader_WhenCalled_ThenHeaderReturned() {
        var result = scl.getOrCreateHeader();
        assertNotNull(result);
        assertEquals("TrainingIEC61850", result.getAttribute("id"));
    }

    @Test
    void getOrCreateHeader_WhenCalledNotContainingHeader_ThenHeaderCreatedAndReturned() {
        var xml = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" version=\"2007\" revision=\"B\"></SCL>";
        var converter = new ElementConverter();
        var sclElement = converter.convertToElement(xml, SCL_ELEMENT_NAME, SCL_NS_URI);
        scl = new GenericSCL(sclElement);

        var result = scl.getOrCreateHeader();
        assertNotNull(result);
        assertNull(result.getAttribute("id"));
    }
}