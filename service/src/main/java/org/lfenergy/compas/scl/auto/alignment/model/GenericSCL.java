// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.Optional;

public class GenericSCL extends AbstractCompasEntity {
    public GenericSCL(Element element) {
        super(element);
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
