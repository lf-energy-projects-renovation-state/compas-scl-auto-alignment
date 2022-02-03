// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericConductingEquipmentTest.CE_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericConnectivityNodeTest.CN_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevelTest.VOLTAGE_LEVEL_NAME;

class GenericBayTest {
    public static final String BAY_NAME = "Bay A";
    public static final String BUSBAR_NAME = "BusBar A";

    private GenericVoltageLevel voltageLevel;
    private GenericBay bay;
    private GenericBay busbar;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        voltageLevel = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
        busbar = voltageLevel.getBays().get(0);
        bay = voltageLevel.getBays().get(3);
    }

    @Test
    void constructorBusbar_WhenCreated_ThenElementSet() {
        assertNotNull(busbar.getElement());
        assertEquals(BUSBAR_NAME, busbar.getName());
    }

    @Test
    void constructorBay_WhenCreated_ThenElementSet() {
        assertNotNull(bay.getElement());
        assertEquals(BAY_NAME, bay.getName());
    }

    @Test
    void isBusbar_WhenCalledOnAllBays_ThenExpectedResult() {
        var bayNr = 0;
        for (var bayOrBusbar : voltageLevel.getBays()) {
            // First 2 bays are Busbars, others are Bays.
            if (bayNr <= 1) {
                assertTrue(bayOrBusbar.isBusbar());
            } else {
                assertFalse(bayOrBusbar.isBusbar());
            }
            bayNr++;
        }
    }

    @Test
    void isBusbar_WhenCalledOnBusbar_ThenTrueReturned() {
        assertTrue(busbar.isBusbar());
    }

    @Test
    void isBusbar_WhenCalledOnOtherBays_ThenFalseReturned() {
        assertFalse(bay.isBusbar());
    }

    @Test
    void getConnectivityNodes_WhenCalled_ThenListReturned() {
        var result = bay.getConnectivityNodes();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(CN_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, bay.getConnectivityNodes());
    }

    @Test
    void getConductingEquipments_WhenCalled_ThenListReturned() {
        var result = bay.getConductingEquipments();

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals(CE_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, bay.getConductingEquipments());
    }
}