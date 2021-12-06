// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.RawGraphBuilder;
import com.powsybl.sld.model.*;
import com.powsybl.sld.model.SwitchNode.SwitchKind;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SclAutoAlignmentGraphBuilder {
    private RawGraphBuilder rawGraphBuilder;
    private RawGraphBuilder.SubstationBuilder substationBuilder;

    private Map<String, RawGraphBuilder.VoltageLevelBuilder> path2VoltageLevelBuilder = new HashMap<>();
    private Map<String, Node> path2Node = new HashMap<>();

    public RawGraphBuilder.SubstationBuilder getSubstationBuilder() {
        return substationBuilder;
    }

    public SclAutoAlignmentGraphBuilder(GenericSubstation substation) {
        rawGraphBuilder = new RawGraphBuilder();
        substationBuilder = rawGraphBuilder.createSubstationBuilder(substation.getPathName());
        substation.getVoltageLevels()
                .forEach(voltageLevel -> createVoltageLevelGraph(substation, voltageLevel));
        substation.getPowerTransformers()
                .forEach(powerTransformer -> createPowerTransformer(substation, powerTransformer));
    }

    private void createPowerTransformer(GenericSubstation substation, GenericPowerTransformer powerTransformer) {
        if (powerTransformer.isFeeder2WT()) {
            var tws = powerTransformer.getTransformerWindings();
            substationBuilder.getSsGraph().addMultiTermNode(
                    Middle2WTNode.create(powerTransformer.getPathName(),
                            powerTransformer.getPathName(),
                            substationBuilder.getSsGraph(),
                            getFeeder2WTLegNode(tws.get(0)),
                            getFeeder2WTLegNode(tws.get(1)),
                            getVoltageLevelBuilder(substation, tws.get(0)).getGraph().getVoltageLevelInfos(),
                            getVoltageLevelBuilder(substation, tws.get(1)).getGraph().getVoltageLevelInfos()));
//            substationBuilder.createFeeder2WT(powerTransformer.getPathName(),
//                    getVoltageLevelBuilder(substation, tws.get(0)),
//                    getVoltageLevelBuilder(substation, tws.get(1)));
        } else if (powerTransformer.isFeeder3WT()) {
            var tws = powerTransformer.getTransformerWindings();
            substationBuilder.getSsGraph().addMultiTermNode(
                    Middle3WTNode.create(powerTransformer.getPathName(),
                            powerTransformer.getPathName(),
                            substationBuilder.getSsGraph(),
                            getFeeder3WTLegNode(tws.get(0)),
                            getFeeder3WTLegNode(tws.get(1)),
                            getFeeder3WTLegNode(tws.get(2)),
                            getVoltageLevelBuilder(substation, tws.get(0)).getGraph().getVoltageLevelInfos(),
                            getVoltageLevelBuilder(substation, tws.get(1)).getGraph().getVoltageLevelInfos(),
                            getVoltageLevelBuilder(substation, tws.get(2)).getGraph().getVoltageLevelInfos()));
//            substationBuilder.createFeeder3WT(powerTransformer.getPathName(),
//                    getVoltageLevelBuilder(substation, tws.get(0)),
//                    getVoltageLevelBuilder(substation, tws.get(1)),
//                    getVoltageLevelBuilder(substation, tws.get(2)));
        }
    }

    private Feeder2WTLegNode getFeeder2WTLegNode(GenericTransformerWinding transformerWinding) {
        var connectivityNode = transformerWinding.getTerminals().get(0).getConnectivityNode();
        return (Feeder2WTLegNode) path2Node.get(connectivityNode);
    }

    private Feeder3WTLegNode getFeeder3WTLegNode(GenericTransformerWinding transformerWinding) {
        var connectivityNode = transformerWinding.getTerminals().get(0).getConnectivityNode();
        return (Feeder3WTLegNode) path2Node.get(connectivityNode);
    }

    private RawGraphBuilder.VoltageLevelBuilder getVoltageLevelBuilder(GenericSubstation substation,
                                                                       GenericTransformerWinding transformerWinding) {
        var connectivityNode = transformerWinding.getTerminals().get(0).getConnectivityNode();
        var voltageLevel = substation.getVoltageLevels()
                .stream()
                .map(GenericVoltageLevel::getBays)
                .flatMap(List::stream)
                .map(GenericBay::getConductingEquipments)
                .flatMap(List::stream)
                .map(GenericConductingEquipment::getTerminals)
                .flatMap(List::stream)
                .filter(terminal -> connectivityNode.equals(terminal.getConnectivityNode()))
                .findFirst()
                .map(terminal -> ((GenericConductingEquipment) terminal.getParent()).getParent().getParent())
                .get();
        return path2VoltageLevelBuilder.get(voltageLevel.getPathName());
    }

    private void createVoltageLevelGraph(GenericSubstation substation,
                                         GenericVoltageLevel voltageLevel) {
        var voltageLevelBuilder =
                rawGraphBuilder.createVoltageLevelBuilder(voltageLevel.getPathName(), voltageLevel.getVoltage(), false);

        // First process the Busbars.
        AtomicInteger busbarIndex = new AtomicInteger(1);
        voltageLevel.getBays().stream()
                .filter(GenericBay::isBusbar)
                .forEach(bay -> createBusbarNode(voltageLevelBuilder, bay, busbarIndex));

        // Next process the other bays.
        voltageLevel.getBays().stream()
                .filter(bay -> !bay.isBusbar())
                .forEach(bay -> createBayNode(voltageLevelBuilder, substation, bay));

        substationBuilder.addVlBuilder(voltageLevelBuilder);
        path2VoltageLevelBuilder.put(voltageLevel.getPathName(), voltageLevelBuilder);
    }

    private void createBusbarNode(RawGraphBuilder.VoltageLevelBuilder voltageLevelBuilder,
                                  GenericBay bay,
                                  AtomicInteger busbarIndex) {
        bay.getConnectivityNodes()
                .forEach(connectivityNode -> path2Node.put(connectivityNode.getPathName(),
                        voltageLevelBuilder.createBusBarSection(bay.getPathName(), busbarIndex.getAndIncrement(), 1)));
    }

    private void createBayNode(RawGraphBuilder.VoltageLevelBuilder voltageLevelBuilder,
                               GenericSubstation substation,
                               GenericBay bay) {
        bay.getConnectivityNodes()
                .forEach(connectivityNode -> {
                    var powerTransformer = substation.getPowerTransformerByConnectivityNode(connectivityNode.getPathName());
                    if (powerTransformer != null) {
                        if (powerTransformer.isFeeder2WT()) {
                            path2Node.put(connectivityNode.getPathName(),
                                    voltageLevelBuilder.createFeeder2wtLegNode(connectivityNode.getPathName(),
                                            powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                        } else if (powerTransformer.isFeeder3WT()) {
                            path2Node.put(connectivityNode.getPathName(),
                                    voltageLevelBuilder.createFeeder3wtLegNode(connectivityNode.getPathName(),
                                            powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                        }
                    } else {
                        path2Node.put(connectivityNode.getPathName(),
                                voltageLevelBuilder.createFictitiousNode(connectivityNode.getPathName()));
                    }
                });

        bay.getConductingEquipments().forEach(ce -> {
            List<GenericTerminal> terminals = ce.getTerminals();

            var pathName = ce.getPathName();
            var node = voltageLevelBuilder.createSwitchNode(SwitchKind.BREAKER, pathName, false, false);

            Node node1 = terminalToNode(voltageLevelBuilder, terminals.get(0));
            Node node2 = null;
            double termNb = terminals.size();
            if (termNb == 1) {
                node2 = voltageLevelBuilder.createLoad(pathName + "/Grounded");
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
