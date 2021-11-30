// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class GenericBay extends AbstractCompasNameEntity {
    public GenericBay(Element element) {
        super(element);
    }

    public boolean isBusbar() {
        return getConnectivityNodes().size() == 1;
    }

    public List<GenericConnectivityNode> getConnectivityNodes() {
        return getElementsStream("ConnectivityNode")
                .map(GenericConnectivityNode::new)
                .collect(Collectors.toList());
    }

    public List<GenericConductingEquipment> getConductingEquipments() {
        return getElementsStream("ConductingEquipment")
                .map(GenericConductingEquipment::new)
                .collect(Collectors.toList());
    }
}
