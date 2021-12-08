// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import java.util.Arrays;

public enum ConductingEquipmentType {
    DIS("DIS"),
    CBR("CBR");

    private final String typeName;

    ConductingEquipmentType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static ConductingEquipmentType fromString(String strType) {
        return Arrays.stream(ConductingEquipmentType.values())
                .filter(type -> type.getTypeName().equals(strType))
                .findFirst()
                .orElse(null);
    }
}
