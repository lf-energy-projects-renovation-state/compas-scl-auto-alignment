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
import static org.lfenergy.compas.scl.auto.alignment.model.GenericTerminalTest.TERMINAL_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevelTest.VOLTAGE_LEVEL_NAME;

class GenericConductingEquipmentTest {
    public static final String CE_NAME = "QB1";

    private GenericConductingEquipment conductingEquipment;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        var voltageLevel = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
        var bay = voltageLevel.getBays().get(2);
        conductingEquipment = bay.getConductingEquipments().get(0);
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(conductingEquipment.getElement());
        assertEquals(CE_NAME, conductingEquipment.getName());
    }

    @Test
    void getType_WhenCalled_ThenTypeReturned() {
        assertEquals(ConductingEquipmentType.DIS.getTypeName(), conductingEquipment.getType());
    }

    @Test
    void getTerminals_WhenCalled_ThenListReturned() {
        var result = conductingEquipment.getTerminals();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TERMINAL_NAME, result.get(0).getName());
    }
}