// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

public class AbstractGenericNameEntity<P extends GenericEntity> extends AbstractGenericEntity<P> {
    public AbstractGenericNameEntity(P parent, Element element) {
        super(parent, element);
    }

    public String getName() {
        return element.getAttribute("name");
    }

    @Override
    public String getPathName() {
        return parent.getPathName() + "/" + getName();
    }
}
