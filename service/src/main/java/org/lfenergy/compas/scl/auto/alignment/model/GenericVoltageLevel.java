// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_VOLTAGE_FOUND_ERROR_CODE;

public class GenericVoltageLevel extends AbstractGenericNameEntity<GenericSubstation> {
    private List<GenericBay> bays;

    public GenericVoltageLevel(GenericSubstation parent, Element element) {
        super(parent, element);
    }

    public double getVoltage() {
        return getElementsStream("Voltage")
                .findFirst()
                .map(this::convertStringToDouble)
                .orElseThrow(() -> new SclAutoAlignmentException(NO_VOLTAGE_FOUND_ERROR_CODE,
                        "No Voltage found for VoltageLevel '" + getName() + "'."));
    }

    public List<GenericBay> getBays() {
        if (bays == null) {
            bays = getElementsStream("Bay")
                    .map(element -> new GenericBay(this, element))
                    .collect(Collectors.toList());
        }
        return bays;
    }

    public Optional<GenericBay> getBusbarByPathName(String pathName) {
        if (StringUtils.isNotBlank(pathName)) {
            return getBays().stream()
                    .filter(GenericBay::isBusbar)
                    .filter(busbar -> pathName.equals(busbar.getPathName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<GenericConductingEquipment> getConductingEquipmentByPathName(String pathName) {
        if (StringUtils.isNotBlank(pathName)) {
            return getBays().stream()
                    .map(GenericBay::getConductingEquipments)
                    .flatMap(List::stream)
                    .filter(conductingEquipment -> pathName.equals(conductingEquipment.getPathName()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
