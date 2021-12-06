// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericSubstation extends AbstractGenericNameEntity<GenericSCL> {
    private List<GenericPowerTransformer> powerTransformers;
    private List<GenericVoltageLevel> voltageLevels;

    public GenericSubstation(GenericSCL parent, Element element) {
        super(parent, element);
    }

    @Override
    public String getPathName() {
        return getName();
    }

    public List<GenericPowerTransformer> getPowerTransformers() {
        if (powerTransformers == null) {
            powerTransformers = getElementsStream("PowerTransformer")
                    .map(element -> new GenericPowerTransformer(this, element))
                    .collect(Collectors.toList());
        }
        return powerTransformers;
    }

    public List<GenericVoltageLevel> getVoltageLevels() {
        if (voltageLevels == null) {
            voltageLevels = getElementsStream("VoltageLevel")
                    .map(element -> new GenericVoltageLevel(this, element))
                    .collect(Collectors.toList());
        }
        return voltageLevels;
    }

    public Optional<GenericVoltageLevel> getVoltageLevel(String name) {
        if (StringUtils.isNotBlank(name)) {
            return getVoltageLevels().stream()
                    .filter(voltageLevel -> name.equals(voltageLevel.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<GenericVoltageLevel> getVoltageLevelByPathName(String pathName) {
        if (StringUtils.isNotBlank(pathName)) {
            return getVoltageLevels().stream()
                    .filter(voltageLevel -> pathName.equals(voltageLevel.getPathName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public GenericPowerTransformer getPowerTransformerByConnectivityNode(String connectivityNode) {
        if (StringUtils.isNotBlank(connectivityNode)) {
            return getPowerTransformers().stream()
                    .map(GenericPowerTransformer::getTransformerWindings)
                    .flatMap(Collection::stream)
                    .map(GenericTransformerWinding::getTerminals)
                    .flatMap(Collection::stream)
                    .filter(terminal -> connectivityNode.equals(terminal.getConnectivityNode()))
                    .findFirst()
                    .map(terminal -> ((GenericTransformerWinding) terminal.getParent()).getParent())
                    .orElse(null);
        }
        return null;
    }
}
