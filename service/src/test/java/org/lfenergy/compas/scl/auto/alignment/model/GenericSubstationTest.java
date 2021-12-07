// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericPowerTransformerTest.PT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevelTest.VOLTAGE_LEVEL_NAME;

class GenericSubstationTest {
    public static final String SUBSTATION_NAME = "AA1";

    private GenericSubstation substation;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        substation = scl.getSubstation(SUBSTATION_NAME).get();
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(substation.getElement());
        assertEquals(SUBSTATION_NAME, substation.getName());
    }

    @Test
    void getFullName_WhenCalled_TheNameReturned() {
        assertEquals(substation.getName(), substation.getFullName());
    }

    @Test
    void getPowerTransformers_WhenCalled_ThenPowerTransformersReturned() throws IOException {
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));
        substation = scl.getSubstation("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4").get();

        var result = substation.getPowerTransformers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PT_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, substation.getPowerTransformers());
    }

    @Test
    void getPowerTransformerByConnectivityNode_WhenCalledWithKnownCN_ThenPowerTransformerReturned() throws IOException {
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));
        substation = scl.getSubstation("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4").get();

        var result = substation.getPowerTransformerByConnectivityNode("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 30kV/BAY_T4_0/CONNECTIVITY_NODE78");

        assertTrue(result.isPresent());
        assertEquals(PT_NAME, result.get().getName());
    }

    @Test
    void getPowerTransformerByConnectivityNode_WhenCalledWithEmptyString_ThenEmptyOptionalReturned() {
        var result = substation.getPowerTransformerByConnectivityNode("");

        assertFalse(result.isPresent());
    }

    @Test
    void getVoltageLevels_WhenCalled_ThenVoltageLevelsReturned() {
        var result = substation.getVoltageLevels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VOLTAGE_LEVEL_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, substation.getVoltageLevels());
    }

    @Test
    void getVoltageLevel_WhenCalled_ThenSpecificVoltageLevelReturned() {
        var result = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME);

        assertTrue(result.isPresent());
        assertEquals(VOLTAGE_LEVEL_NAME, result.get().getName());
    }

    @Test
    void getVoltageLevel_WhenCalledWithBlankName_ThenEmptyOptional() {
        var result = substation.getVoltageLevel("");

        assertFalse(result.isPresent());
    }

    @Test
    void getVoltageLevelByFullName_WhenCalled_ThenSpecificVoltageLevelReturned() {
        var result = substation.getVoltageLevelByFullName("AA1/J1");

        assertTrue(result.isPresent());
        assertEquals(VOLTAGE_LEVEL_NAME, result.get().getName());
    }

    @Test
    void getVoltageLevelByFullName_WhenCalledWithBlankName_ThenEmptyOptional() {
        var result = substation.getVoltageLevelByFullName("");

        assertFalse(result.isPresent());
    }
}