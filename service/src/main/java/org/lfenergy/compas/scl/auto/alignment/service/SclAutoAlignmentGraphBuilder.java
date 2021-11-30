// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.RawGraphBuilder;
import com.powsybl.sld.model.Node;
import com.powsybl.sld.model.SwitchNode.SwitchKind;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SclAutoAlignmentGraphBuilder {
    private RawGraphBuilder rawGraphBuilder;
    private RawGraphBuilder.SubstationBuilder substationBuilder;
    private Map<String, Node> path2Node = new HashMap<>();

    public RawGraphBuilder.SubstationBuilder getSubstationBuilder() {
        return substationBuilder;
    }

    public SclAutoAlignmentGraphBuilder(GenericSubstation substationElement) {
        rawGraphBuilder = new RawGraphBuilder();
        substationBuilder = rawGraphBuilder.createSubstationBuilder(substationElement.getName());
        substationElement.getVoltageLevels()
                .forEach(voltageLevel -> createVoltageLevelGraph(voltageLevel));
    }

    private void createVoltageLevelGraph(GenericVoltageLevel voltageLevel) {
        var voltageLevelBuilder =
                rawGraphBuilder.createVoltageLevelBuilder(voltageLevel.getName(), voltageLevel.getVoltage(), false);

        // First process the Busbars.
        AtomicInteger busbarIndex = new AtomicInteger(1);
        voltageLevel.getBays().stream()
                .filter(GenericBay::isBusbar)
                .forEach(bay -> createBusbarNode(voltageLevelBuilder, bay, busbarIndex));

        // Next process the other bays.
        voltageLevel.getBays().stream()
                .filter(bay -> !bay.isBusbar())
                .forEach(bay -> createBayNode(voltageLevelBuilder, bay));

        substationBuilder.addVlBuilder(voltageLevelBuilder);
    }

    private void createBusbarNode(RawGraphBuilder.VoltageLevelBuilder voltageLevelBuilder,
                                  GenericBay bay,
                                  AtomicInteger busbarIndex) {
        bay.getConnectivityNodes()
                .forEach(connectivityNode ->
                        path2Node.put(connectivityNode.getPathName(),
                                voltageLevelBuilder.createBusBarSection(bay.getName(), busbarIndex.getAndIncrement(), 1)));
    }

    private void createBayNode(RawGraphBuilder.VoltageLevelBuilder voltageLevelBuilder,
                               GenericBay bay) {
        bay.getConnectivityNodes()
                .forEach(connectivityNode ->
                        path2Node.put(connectivityNode.getPathName(),
                                voltageLevelBuilder.createFictitiousNode(connectivityNode.getPathName())));

        bay.getConductingEquipments().forEach(ce -> {
            List<GenericTerminal> terminals = ce.getTerminals();

            Node node;
            var name = ce.getName();
            var type = ConductingEquipmentType.fromString(ce.getType());
            if (type == ConductingEquipmentType.DIS) {
                node = voltageLevelBuilder.createSwitchNode(SwitchKind.DISCONNECTOR, name, false, false);
            } else if (type == ConductingEquipmentType.CBR) {
                node = voltageLevelBuilder.createSwitchNode(SwitchKind.BREAKER, name, false, false);
            } else {
                node = voltageLevelBuilder.createFictitiousNode(name);
            }

            Node node1 = terminalToNode(voltageLevelBuilder, terminals.get(0));
            Node node2 = null;
            double termNb = terminals.size();
            if (termNb == 1) {
                node2 = voltageLevelBuilder.createLoad(name);
            } else if (termNb == 2) {
                node2 = terminalToNode(voltageLevelBuilder, terminals.get(1));
            }
            voltageLevelBuilder.connectNode(node, node1);
            voltageLevelBuilder.connectNode(node, node2);
        });
    }

    private Node terminalToNode(RawGraphBuilder.VoltageLevelBuilder voltageLevelBuilder,
                                GenericTerminal terminal) {
        String pathName = terminal.getConnectivityNode();
        if (pathName != null) {
            return path2Node.get(pathName);
        }
        return voltageLevelBuilder.createLoad(terminal.getCNodeName());
    }
}
