// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_PREFIX;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;

class GenericSCLTest {
    public static final String BASIC_SCD_FILENAME = "scl-1.scd";

    @Test
    void constructor_WhenObjectCreatedAlreadyContainingSXYNamespace_ThenElementSetAndNamespaceStillThere() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));

        assertNotNull(scl.getElement());
        assertEquals(SCLXY_NS_URI, scl.getElement().getAttribute("xmlns:" + SCLXY_PREFIX));
    }

    @Test
    void constructor_WhenObjectCreatedAlreadyWithoutSXYNamespace_ThenElementSetAndNamespaceSet() throws IOException {
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));

        assertNotNull(scl.getElement());
        assertEquals(SCLXY_NS_URI, scl.getElement().getAttribute("xmlns:" + SCLXY_PREFIX));
    }

    @Test
    void getSubstation_WhenCalledOnExampleSCL_ThenFirstSubstationIsReturned() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));

        var result = scl.getSubstation(SUBSTATION_NAME);

        assertTrue(result.isPresent());
        assertEquals(SUBSTATION_NAME, result.get().getName());
    }

    @Test
    void getSubstation_WhenCalledWithBlankName_ThenEmptyOptional() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));

        var result = scl.getSubstation("");

        assertFalse(result.isPresent());
    }
}