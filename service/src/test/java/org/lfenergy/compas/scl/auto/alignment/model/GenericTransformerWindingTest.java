// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;

class GenericTransformerWindingTest {
    public static final String PTW_NAME = "T4_3";

    private GenericTransformerWinding powerTransformerWinding;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));
        var substation = scl.getSubstation("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4").get();
        var powerTransformer = substation.getPowerTransformers().get(0);
        powerTransformerWinding = powerTransformer.getTransformerWindings().get(0);
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(powerTransformerWinding.getElement());
        assertEquals(PTW_NAME, powerTransformerWinding.getName());
    }

    @Test
    void getTerminals_WhenCalled_ThenListReturned() {
        var result = powerTransformerWinding.getTerminals();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("T4_0", result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, powerTransformerWinding.getTerminals());
    }

}