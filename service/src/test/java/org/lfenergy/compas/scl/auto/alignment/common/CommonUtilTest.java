// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.common;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;

class CommonUtilTest {
    private ElementConverter converter = new ElementConverter();

    @Test
    void constructor_WhenConstructorCalled_ThenShouldThrowExceptionCauseForbidden() {
        assertThrows(UnsupportedOperationException.class, CommonUtil::new);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithoutSXYCoordinates_ThenSameDataReturned() {
        var sclString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">" +
                "  <Substation desc=\"Substation\" name=\"AA1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"J1\">" +
                "      <Bay name=\"BusBar A\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"AA1/J1/BusBar A/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation></SCL>";
        var sclElement = converter.convertToElement(sclString, SCL_ELEMENT_NAME, SCL_NS_URI);
        var substationElement = getSubstationElement(sclElement);

        // Cleanup the X/Y Coordinate data.
        CommonUtil.cleanSXYDeclarationAndAttributes(substationElement);
        var result = converter.convertToString(sclElement);

        assertNotNull(result);
        assertEquals(sclString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithSXYCoordinatesDefaultPrefix_ThenDataRemoved() {
        var sclString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation name=\"AA1\" desc=\"Substation\">" +
                "    <VoltageLevel sxy:x=\"1\" sxy:y=\"3\" name=\"J1\" desc=\"Voltage Level\">" +
                "      <Bay sxy:x=\"1\" sxy:y=\"1\" name=\"BusBar A\">" +
                "        <ConnectivityNode pathName=\"AA1/J1/BusBar A/L1\" name=\"L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "</SCL>";
        var expectedString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation desc=\"Substation\" name=\"AA1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"J1\">" +
                "      <Bay name=\"BusBar A\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"AA1/J1/BusBar A/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation></SCL>";
        var sclElement = converter.convertToElement(sclString, SCL_ELEMENT_NAME, SCL_NS_URI);
        var substationElement = getSubstationElement(sclElement);

        // Cleanup the X/Y Coordinate data.
        CommonUtil.cleanSXYDeclarationAndAttributes(substationElement);
        var result = converter.convertToString(sclElement);

        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithSXYCoordinatesWithDifferentPrefix_ThenDataRemoved() {
        var sclString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:somexy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation name=\"AA1\" desc=\"Substation\">" +
                "    <VoltageLevel somexy:x=\"1\" somexy:y=\"3\" name=\"J1\" desc=\"Voltage Level\">" +
                "      <Bay somexy:x=\"1\" somexy:y=\"1\" name=\"BusBar A\">" +
                "        <ConnectivityNode pathName=\"AA1/J1/BusBar A/L1\" name=\"L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "</SCL>";
        var expectedString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:somexy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation desc=\"Substation\" name=\"AA1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"J1\">" +
                "      <Bay name=\"BusBar A\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"AA1/J1/BusBar A/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation></SCL>";
        var sclElement = converter.convertToElement(sclString, SCL_ELEMENT_NAME, SCL_NS_URI);
        var substationElement = getSubstationElement(sclElement);

        // Cleanup the X/Y Coordinate data.
        CommonUtil.cleanSXYDeclarationAndAttributes(substationElement);
        var result = converter.convertToString(sclElement);

        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithNamespaceDeclarationOnSubstationElement_ThenDeclarationRemovedRemoved() {
        var sclString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">" +
                "  <Substation name=\"AA1\" desc=\"Substation\" xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "    <VoltageLevel sxy:x=\"1\" sxy:y=\"3\" name=\"J1\" desc=\"Voltage Level\">" +
                "      <Bay sxy:x=\"1\" sxy:y=\"1\" name=\"BusBar A\">" +
                "        <ConnectivityNode pathName=\"AA1/J1/BusBar A/L1\" name=\"L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "</SCL>";
        var expectedString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">" +
                "  <Substation desc=\"Substation\" name=\"AA1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"J1\">" +
                "      <Bay name=\"BusBar A\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"AA1/J1/BusBar A/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation></SCL>";
        var sclElement = converter.convertToElement(sclString, SCL_ELEMENT_NAME, SCL_NS_URI);
        var substationElement = getSubstationElement(sclElement);

        // Cleanup the X/Y Coordinate data.
        CommonUtil.cleanSXYDeclarationAndAttributes(substationElement);
        var result = converter.convertToString(sclElement);

        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    @Test
    void cleanSXYDeclarationAndAttributes_WhenCalledWithMultipleSubstations_ThenOnlyOneSubstationCleaned() {
        var sclString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation name=\"AA1\" desc=\"Substation\">" +
                "    <VoltageLevel sxy:x=\"1\" sxy:y=\"3\" name=\"J1\" desc=\"Voltage Level\">" +
                "      <Bay sxy:x=\"1\" sxy:y=\"1\" name=\"BusBar A\">" +
                "        <ConnectivityNode pathName=\"AA1/J1/BusBar A/L1\" name=\"L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "  <Substation name=\"BB1\" desc=\"Substation\">" +
                "    <VoltageLevel sxy:x=\"1\" sxy:y=\"3\" name=\"K1\" desc=\"Voltage Level\">" +
                "      <Bay sxy:x=\"1\" sxy:y=\"1\" name=\"BusBar B\">" +
                "        <ConnectivityNode pathName=\"BB1/K1/BusBar B/L1\" name=\"L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "</SCL>";
        var expectedString = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:sxy=\"http://www.iec.ch/61850/2003/SCLcoordinates\">" +
                "  <Substation desc=\"Substation\" name=\"AA1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"J1\">" +
                "      <Bay name=\"BusBar A\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"AA1/J1/BusBar A/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "  <Substation desc=\"Substation\" name=\"BB1\">" +
                "    <VoltageLevel desc=\"Voltage Level\" name=\"K1\" sxy:x=\"1\" sxy:y=\"3\">" +
                "      <Bay name=\"BusBar B\" sxy:x=\"1\" sxy:y=\"1\">" +
                "        <ConnectivityNode name=\"L1\" pathName=\"BB1/K1/BusBar B/L1\"/>" +
                "      </Bay>" +
                "    </VoltageLevel>" +
                "  </Substation>" +
                "</SCL>";

        var sclElement = converter.convertToElement(sclString, SCL_ELEMENT_NAME, SCL_NS_URI);
        var substationElement = getSubstationElement(sclElement);

        // Cleanup the X/Y Coordinate data.
        CommonUtil.cleanSXYDeclarationAndAttributes(substationElement);
        var result = converter.convertToString(sclElement);

        assertNotNull(result);
        assertEquals(expectedString, result);
    }

    private Element getSubstationElement(Element sclElement) {
        return ElementUtil.getElementsStream(sclElement, "Substation")
                .filter(element -> "AA1".equals(element.getAttribute("name")))
                .findFirst()
                .orElseThrow();
    }
}