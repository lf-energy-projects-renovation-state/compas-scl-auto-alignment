// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.Optional;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_PREFIX;

public class GenericSCL extends AbstractCompasEntity {
    public GenericSCL(Element element) {
        super(element);

        if (!element.hasAttribute("xmlns:" + SCLXY_PREFIX)) {
            element.setAttribute("xmlns:" + SCLXY_PREFIX, SCLXY_NS_URI);
        }
    }

    public Optional<GenericSubstation> getSubstation(String substationName) {
        if (StringUtils.isNotBlank(substationName)) {
            return getElementsStream("Substation")
                    .map(GenericSubstation::new)
                    .filter(substation -> substationName.equals(substation.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
