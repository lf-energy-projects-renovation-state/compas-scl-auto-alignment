// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.common;

import java.util.regex.Pattern;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;

public class CommonUtil {
    CommonUtil() {
        throw new UnsupportedOperationException("CommonUtil class");
    }

    public static String cleanSXYDeclarationAndAttributes(String data) {
        // Find Prefix of Namespace.
        var pattern = Pattern.compile("xmlns:([A-Za-z0-9]*)=\\\"" + SCLXY_NS_URI + "\\\"");
        var matcher = pattern.matcher(data);

        if (matcher.find()) {
            var prefix = matcher.group(1);
            var replacementPattern = "xmlns:[A-Za-z0-9]*=\\\"" + SCLXY_NS_URI + "\\\"" + // Remove the namespace declaration.
                    "|" + // Combine the two regex patterns.
                    "[ ]?" + prefix + ":[A-Za-z]*=\\\"[A-Za-z0-9]*\\\""; // Remove the attributes using that namespace.
            return data.replaceAll(replacementPattern, "");
        }
        return data;
    }
}
