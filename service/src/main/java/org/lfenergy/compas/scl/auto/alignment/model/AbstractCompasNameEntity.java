// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

public class AbstractCompasNameEntity extends AbstractCompasEntity {
    public AbstractCompasNameEntity(Element element) {
        super(element);
    }

    public String getName() {
        return element.getAttribute("name");
    }
}
