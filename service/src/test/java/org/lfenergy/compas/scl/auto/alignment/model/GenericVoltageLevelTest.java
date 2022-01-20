// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_VOLTAGE_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericBayTest.BAY_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericBayTest.BUSBAR_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;

class GenericVoltageLevelTest {
    public static final String VOLTAGE_LEVEL_NAME = "J1";

    private GenericVoltageLevel voltageLevel;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        voltageLevel = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(voltageLevel.getElement());
        assertEquals(VOLTAGE_LEVEL_NAME, voltageLevel.getName());
    }

    @Test
    void getVoltage_WhenCalled_ThenCorrectVoltageReturned() {
        var result = voltageLevel.getVoltage();

        assertEquals(20, result);
    }

    @Test
    void getVoltage_WhenCalledContainingNoVoltage_ThenExceptionThrown() {
        // Remove the Voltage Element from the VoltageLevel to create exception situation.
        var voltageElement = voltageLevel.getElement().getElementsByTagNameNS(SCL_NS_URI, "Voltage").item(0);
        voltageLevel.getElement().removeChild(voltageElement);

        var exception = assertThrows(SclAutoAlignmentException.class, () -> voltageLevel.getVoltage());
        assertEquals(NO_VOLTAGE_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void getBays_WhenCalled_ThenVoltageLevelsReturned() {
        var result = voltageLevel.getBays();

        assertNotNull(result);
        assertEquals(7, result.size());
        assertEquals(BUSBAR_NAME, result.get(0).getName());
        assertEquals(BAY_NAME, result.get(2).getName());
    }
}