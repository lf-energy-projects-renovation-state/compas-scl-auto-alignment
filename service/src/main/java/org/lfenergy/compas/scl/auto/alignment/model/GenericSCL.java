// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;

public class GenericSCL implements GenericEntity {
    private final Element element;
    private List<GenericSubstation> substations;

    public GenericSCL(Element element) {
        this.element = element;

        if (!element.hasAttribute("xmlns:" + SCLXY_PREFIX)) {
            element.setAttribute("xmlns:" + SCLXY_PREFIX, SCLXY_NS_URI);
        }
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getPathName() {
        return "";
    }

    public List<GenericSubstation> getSubstations() {
        if (substations == null) {
            var nodeList = element.getElementsByTagNameNS(SCL_NS_URI, "Substation");
            substations = IntStream.range(0, nodeList.getLength())
                    .mapToObj(nodeList::item)
                    .filter(Element.class::isInstance)
                    .map(Element.class::cast)
                    .map(element -> new GenericSubstation(this, element))
                    .collect(Collectors.toList());
        }
        return substations;
    }

    public Optional<GenericSubstation> getSubstation(String substationName) {
        if (StringUtils.isNotBlank(substationName)) {
            return getSubstations().stream()
                    .filter(substation -> substationName.equals(substation.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
