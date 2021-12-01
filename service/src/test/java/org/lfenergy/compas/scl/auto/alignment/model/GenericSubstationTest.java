// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
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
    void getVoltageLevels_WhenCalled_ThenVoltageLevelsReturned() {
        var result = substation.getVoltageLevels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VOLTAGE_LEVEL_NAME, result.get(0).getName());
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
}