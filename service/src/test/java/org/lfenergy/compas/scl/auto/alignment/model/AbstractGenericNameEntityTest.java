// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevelTest.VOLTAGE_LEVEL_NAME;

class AbstractGenericNameEntityTest {
    private AbstractGenericNameEntity<GenericSubstation> entity;

    @BeforeEach
    void setup() throws IOException {
        // Use VoltageLevel as example to test generic functionality.
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        entity = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(entity.getElement());
    }

    @Test
    void getName_WhenCalled_ThenNameReturned() {
        assertEquals(VOLTAGE_LEVEL_NAME, entity.getName());
    }
}