// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConductingEquipmentTypeTest {
    @Test
    void fromString_WhenCalledWithKnownType_ThenTypeReturned() {
        Arrays.stream(ConductingEquipmentType.values())
                .forEach(type -> assertEquals(type, ConductingEquipmentType.fromString(type.getTypeName())));
    }

    @Test
    void fromString_WhenCalledWithUnknownType_ThenNullReturned() {
        assertNull(ConductingEquipmentType.fromString("Unknown"));
    }
}