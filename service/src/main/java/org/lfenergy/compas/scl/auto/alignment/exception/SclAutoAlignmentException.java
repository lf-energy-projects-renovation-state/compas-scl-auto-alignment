// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

public class SclAutoAlignmentException extends CompasException {
    public SclAutoAlignmentException(String errorCode, String message) {
        super(errorCode, message);
    }
}
