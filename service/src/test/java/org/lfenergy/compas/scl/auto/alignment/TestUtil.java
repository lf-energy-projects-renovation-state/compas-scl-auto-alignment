// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment;

import org.lfenergy.compas.core.commons.ElementConverter;
import org.w3c.dom.Element;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;

public final class TestUtil {
    private TestUtil() {
        // Only static methods.
    }

    public static Element readSCLElement(String filename) throws IOException {
        var sclData = readSCL(filename);

        ElementConverter converter = new ElementConverter();
        return converter.convertToElement(new BufferedInputStream(
                new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
    }

    public static String readSCL(String filename) throws IOException {
        return readFile("/scl/" + filename);
    }

    public static String readFile(String filename) throws IOException {
        var inputStream = TestUtil.class.getResourceAsStream(filename);
        assert inputStream != null;

        return new String(inputStream.readAllBytes());
    }

    @Deprecated
    public static void writeFile(String fileName, String data) {
        File file = new File("target", fileName);
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            fw.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
