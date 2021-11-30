// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

public class GenericConnectivityNode extends AbstractCompasNameEntity {
    public GenericConnectivityNode(Element element) {
        super(element);
    }

    public String getPathName() {
        return getAttribute("pathName");
    }
}
