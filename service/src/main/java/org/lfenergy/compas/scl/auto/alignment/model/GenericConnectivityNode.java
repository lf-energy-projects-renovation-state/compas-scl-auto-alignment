// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

public class GenericConnectivityNode extends AbstractGenericNameEntity<GenericBay> {
    public GenericConnectivityNode(GenericBay parent, Element element) {
        super(parent, element);
    }

    public String getPathName() {
        return getAttribute("pathName");
    }
}
