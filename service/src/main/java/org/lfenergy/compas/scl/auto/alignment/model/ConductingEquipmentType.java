// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

public enum ConductingEquipmentType {
    DIS("DIS"),
    CBR("CBR");

    private final String typeName;

    ConductingEquipmentType(String typeName) {
        this.typeName = typeName;
    }

    public static ConductingEquipmentType fromString(String strType) {
        switch (strType) {
            case "DIS":
                return DIS;
            case "CBR":
                return CBR;
            default:
                return null;
        }
    }
}
