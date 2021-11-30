// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class GenericSubstation extends AbstractCompasNameEntity {

    public GenericSubstation(Element element) {
        super(element);
    }

    public List<GenericVoltageLevel> getVoltageLevels() {
        return getElementsStream("VoltageLevel")
                .map(GenericVoltageLevel::new)
                .collect(Collectors.toList());
    }
}
