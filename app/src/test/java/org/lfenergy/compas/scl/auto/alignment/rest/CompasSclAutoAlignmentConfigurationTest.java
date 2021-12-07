// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasSclAutoAlignmentConfigurationTest {
    @Test
    void createElementConverter_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclAutoAlignmentConfiguration().createElementConverter());
    }
}