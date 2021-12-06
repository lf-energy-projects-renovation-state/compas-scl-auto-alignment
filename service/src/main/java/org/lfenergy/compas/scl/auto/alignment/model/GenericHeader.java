// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;

public class GenericHeader extends AbstractGenericEntity<GenericSCL> {
    public GenericHeader(GenericSCL parent, Element element) {
        super(parent, element);
    }

    @Override
    public String getFullName() {
        return "";
    }

    public String getVersion() {
        return getAttribute("version");
    }

    public String getRevision() {
        return getAttribute("revision");
    }

    public void addHistoryItem(String who, String fullmessage) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX");
        var history = getOrCreateHistory();

        var document = getElement().getOwnerDocument();
        Element hItem = document.createElementNS(SCL_NS_URI, "Hitem");
        hItem.setAttribute("version", getVersion());
        hItem.setAttribute("revision", "saa");
        hItem.setAttribute("when", formatter.format(new Date()));
        hItem.setAttribute("who", who);
        hItem.setAttribute("what", fullmessage);
        history.appendChild(hItem);
    }

    private Element getOrCreateHistory() {
        var document = getElement().getOwnerDocument();
        return getElementsStream("History")
                .findFirst()
                .orElseGet(() -> {
                    Element newHistory = document.createElementNS(SCL_NS_URI, "History");
                    getElement().appendChild(newHistory);
                    return newHistory;
                });
    }
}
