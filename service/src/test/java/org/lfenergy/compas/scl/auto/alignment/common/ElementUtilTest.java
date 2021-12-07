package org.lfenergy.compas.scl.auto.alignment.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;

class ElementUtilTest {
    public static final String BASIC_SCD_FILENAME = "scl-1.scd";

    @Test
    void constructor_WhenConstructorCalled_ThenShouldThrowExceptionCauseForbidden() {
        assertThrows(UnsupportedOperationException.class, ElementUtil::new);
    }

    @Test
    void getElementsStream_WhenCalledWithSCLElement_ThenRetrieveTheSubstationElement() throws IOException {
        var scl = readSCLElement(BASIC_SCD_FILENAME);

        var result = ElementUtil.getElementsStream(scl, "Substation");
        assertEquals(1, result.count());
    }

    @Test
    void getAttribute_WhenCalledWithSCLElement_ThenRetrieveVersionAttributeFromSCLElement() throws IOException {
        var scl = readSCLElement(BASIC_SCD_FILENAME);

        var result = ElementUtil.getAttribute(scl, "version");
        assertNotNull(result);
        assertEquals("2007", result);
    }

    @Test
    void getAttribute_WhenCalledWithSCLElement_ThenRetrieveEmptyDummyAttributeFromSCLElementAsNull() throws IOException {
        var scl = readSCLElement(BASIC_SCD_FILENAME);

        var result = ElementUtil.getAttribute(scl, "dummy");
        assertNull(result);
    }
}