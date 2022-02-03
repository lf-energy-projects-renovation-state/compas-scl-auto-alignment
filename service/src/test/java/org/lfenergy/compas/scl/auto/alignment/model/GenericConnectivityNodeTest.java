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

class GenericConnectivityNodeTest {
    public static final String CN_NAME = "L1";

    private GenericConnectivityNode connectivityNode;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        var voltageLevel = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
        var bay = voltageLevel.getBays().get(3);
        connectivityNode = bay.getConnectivityNodes().get(0);
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(connectivityNode.getElement());
        assertEquals(CN_NAME, connectivityNode.getName());
    }

    @Test
    void getPathName_WhenCalled_ThenPathNameReturned() {
        assertEquals("AA1/J1/Bay A/L1", connectivityNode.getPathName());
    }
}