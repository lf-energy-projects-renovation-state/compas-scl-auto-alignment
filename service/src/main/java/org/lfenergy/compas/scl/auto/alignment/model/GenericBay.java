// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class GenericBay extends AbstractGenericNameEntity<GenericVoltageLevel> {
    private List<GenericConnectivityNode> connectivityNodes;

    public GenericBay(GenericVoltageLevel parent, Element element) {
        super(parent, element);
    }

    public boolean isBusbar() {
        return getConnectivityNodes().size() == 1;
    }

    public List<GenericConnectivityNode> getConnectivityNodes() {
        if (connectivityNodes == null) {
            connectivityNodes = getElementsStream("ConnectivityNode")
                    .map(element -> new GenericConnectivityNode(this, element))
                    .collect(Collectors.toList());
        }
        return connectivityNodes;
    }

    public List<GenericConductingEquipment> getConductingEquipments() {
        return getElementsStream("ConductingEquipment")
                .map(element -> new GenericConductingEquipment(this, element))
                .collect(Collectors.toList());
    }
}
