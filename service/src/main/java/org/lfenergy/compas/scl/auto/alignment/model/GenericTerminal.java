// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

public class GenericTerminal extends AbstractGenericNameEntity<AbstractGenericNameEntity<?>> {
    public GenericTerminal(AbstractGenericNameEntity<?> parent, Element element) {
        super(parent, element);
    }

    public String getConnectivityNode() {
        return getAttribute("connectivityNode");
    }

    public String getCNodeName() {
        return getAttribute("cNodeName");
    }
}
