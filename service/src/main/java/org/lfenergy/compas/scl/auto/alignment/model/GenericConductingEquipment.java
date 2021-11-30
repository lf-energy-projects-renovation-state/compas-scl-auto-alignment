// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class GenericConductingEquipment extends AbstractCompasNameEntity {
    public GenericConductingEquipment(Element element) {
        super(element);
    }

    public List<GenericTerminal> getTerminals() {
        return getElementsStream("Terminal")
                .map(GenericTerminal::new)
                .collect(Collectors.toList());
    }

    public String getType() {
        return getAttribute("type");
    }
}
