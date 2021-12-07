// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilTest {
    @Test
    void constructor_WhenConstructorCalled_ThenShouldThrowExceptionCauseForbidden() {
        assertThrows(UnsupportedOperationException.class, CommonUtil::new);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithSXYCoordinatesDefaultPrefix_ThenDataRemoved() {
        var sclString = "<SCL xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Bay sxy:x=\"1\" sxy:y=\"2\" name=\"BusBar B\" sxy:dir=\"horizontal\">" +
                "    <ConnectivityNode pathName=\"AA1/J1/BusBar B/L1\" name=\"L1\"/>" +
                "  </Bay>" +
                "</SCL>";
        var expectedString = "<SCL >" +
                "  <Bay name=\"BusBar B\">" +
                "    <ConnectivityNode pathName=\"AA1/J1/BusBar B/L1\" name=\"L1\"/>" +
                "  </Bay>" +
                "</SCL>";

        var result = CommonUtil.cleanSXYDeclarationAndAttributes(sclString);
        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithSXYCoordinatesWithDifferentPrefix_ThenDataRemoved() {
        var sclString = "<SCL xmlns:somexy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Bay somexy:x=\"1\" somexy:y=\"2\" name=\"BusBar B\" somexy:dir=\"horizontal\">" +
                "    <ConnectivityNode pathName=\"AA1/J1/BusBar B/L1\" name=\"L1\"/>" +
                "  </Bay>" +
                "</SCL>";
        var expectedString = "<SCL >" +
                "  <Bay name=\"BusBar B\">" +
                "    <ConnectivityNode pathName=\"AA1/J1/BusBar B/L1\" name=\"L1\"/>" +
                "  </Bay>" +
                "</SCL>";

        var result = CommonUtil.cleanSXYDeclarationAndAttributes(sclString);
        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithoutSXYCoordinates_ThenSameDataReturned() {
        var sclString = "<SCL>" +
                "  <Bay name=\"BusBar B\">" +
                "    <ConnectivityNode pathName=\"AA1/J1/BusBar B/L1\" name=\"L1\"/>" +
                "  </Bay>" +
                "</SCL>";

        var result = CommonUtil.cleanSXYDeclarationAndAttributes(sclString);
        assertNotNull(result);
        assertEquals(sclString, result);
    }
}