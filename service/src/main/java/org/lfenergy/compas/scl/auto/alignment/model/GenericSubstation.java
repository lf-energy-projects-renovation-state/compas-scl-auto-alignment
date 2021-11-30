// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;
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

    public Optional<GenericVoltageLevel> getVoltageLevel(String voltageLevelName) {
        if (StringUtils.isNotBlank(voltageLevelName)) {
            return getElementsStream("VoltageLevel")
                    .map(GenericVoltageLevel::new)
                    .filter(voltageLevel -> voltageLevelName.equals(voltageLevel.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
