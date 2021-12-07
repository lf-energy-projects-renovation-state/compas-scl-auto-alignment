package org.lfenergy.compas.scl.auto.alignment.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_SCL_ELEMENT_FOUND_ERROR_CODE;

class SclAutoAlignmentExceptionTest {
    @Test
    void constructor_WhenCalledWithOnlyMessage_ThenMessageCanBeRetrieved() {
        var expectedMessage = "The message";
        var exception = new SclAutoAlignmentException(NO_SCL_ELEMENT_FOUND_ERROR_CODE, expectedMessage);

        assertEquals(NO_SCL_ELEMENT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());
    }
}