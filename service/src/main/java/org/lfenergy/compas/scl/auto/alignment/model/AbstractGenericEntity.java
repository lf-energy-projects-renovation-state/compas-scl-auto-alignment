// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;

public abstract class AbstractGenericEntity<P extends GenericEntity> implements GenericEntity {
    protected final P parent;
    protected final Element element;

    public AbstractGenericEntity(P parent, Element element) {
        this.parent = parent;
        this.element = element;
    }

    protected Stream<Element> getElementsStream(String tagName) {
        var nodeList = element.getElementsByTagNameNS(SCL_NS_URI, tagName);
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

    @Override
    public Element getElement() {
        return element;
    }

    public P getParent() {
        return parent;
    }

    public void setXYCoordinates(long x, long y) {
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":x", String.valueOf(x));
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":y", String.valueOf(y));
    }
}
