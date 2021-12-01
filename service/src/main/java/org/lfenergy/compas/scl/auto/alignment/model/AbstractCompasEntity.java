// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;

public class AbstractCompasEntity {
    protected Element element;

    public AbstractCompasEntity(Element element) {
        this.element = element;
    }

    protected Stream<Element> getElementsStream(String tagName) {
        return getElementsStream(SCL_NS_URI, tagName);
    }

    protected Stream<Element> getElementsStream(String namespace, String tagName) {
        var nodeList = element.getElementsByTagNameNS(namespace, tagName);
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .filter(Element.class::isInstance)
                .map(Element.class::cast);
    }

    protected String getAttribute(String attributeName) {
        String value = element.getAttribute(attributeName);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

    protected double convertStringToDouble(Element element) {
        return Double.parseDouble(element.getTextContent());
    }

    public Element getElement() {
        return element;
    }

    public void setXYCoordinates(long x, long y) {
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":x", String.valueOf(x));
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":y", String.valueOf(y));
    }
}
