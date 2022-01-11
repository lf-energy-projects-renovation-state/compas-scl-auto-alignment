// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.builder;

import com.powsybl.sld.model.*;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class VoltageLevelGraphBuilder extends AbstractGraphBuilder<VoltageLevelGraph> {
    private final GenericVoltageLevel voltageLevel;
    private final GenericSubstation substation;

    public VoltageLevelGraphBuilder(GenericVoltageLevel voltageLevel) {
        this(voltageLevel, null, new HashMap<>(), true);
    }

    public VoltageLevelGraphBuilder(GenericVoltageLevel voltageLevel,
                                    GenericSubstation substation,
                                    Map<String, Node> path2Node) {
        this(voltageLevel, substation, path2Node, false);
    }

    private VoltageLevelGraphBuilder(GenericVoltageLevel voltageLevel,
                                     GenericSubstation substation,
                                     Map<String, Node> path2Node,
                                     boolean forVoltageLevelDiagram) {
        super(path2Node);
        this.voltageLevel = voltageLevel;
        this.substation = substation;

        var voltageLevelInfos = new VoltageLevelInfos(voltageLevel.getFullName(),
                voltageLevel.getFullName(),
                voltageLevel.getVoltage());
        setGraph(VoltageLevelGraph.create(voltageLevelInfos, forVoltageLevelDiagram));

        createVoltageLevel();
    }

    private void createVoltageLevel() {
        // First process the Busbars.
        var busbarIndex = new AtomicInteger(1);
        voltageLevel.getBays().stream()
                .filter(GenericBay::isBusbar)
                .forEach(busbar -> processBusbarNode(busbar, busbarIndex));

        // Next process the other bays.
        voltageLevel.getBays().stream()
                .filter(bay -> !bay.isBusbar())
                .forEach(this::processBayNode);
    }

    private void processBusbarNode(GenericBay busbar,
                                   AtomicInteger busbarIndex) {
        busbar.getConnectivityNodes()
                .forEach(connectivityNode ->
                        addNode(connectivityNode.getPathName(),
                                createBusbarNode(busbar.getFullName(), busbarIndex.getAndIncrement(), 1)));
    }

    public BusNode createBusbarNode(String id, int busbarIndex, int sectionIndex) {
        BusNode busNode = BusNode.create(getGraph(), id, id);
        getGraph().addNode(busNode);
        busNode.setBusBarIndexSectionIndex(busbarIndex, sectionIndex);
        return busNode;
    }

    private void processBayNode(GenericBay bay) {
        bay.getConnectivityNodes().forEach(this::createConnectivityNode);
        bay.getConductingEquipments().forEach(this::processConductingEquipment);
    }

    private void createConnectivityNode(GenericConnectivityNode connectivityNode) {
        getPowerTransformer(connectivityNode.getPathName())
                .ifPresentOrElse(powerTransformer -> {
                            if (powerTransformer.isFeeder2WT()) {
                                addNode(connectivityNode.getPathName(),
                                        createFeeder2WTLegNode(connectivityNode.getPathName(),
                                                powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                            } else if (powerTransformer.isFeeder3WT()) {
                                addNode(connectivityNode.getPathName(),
                                        createFeeder3WTLegNode(connectivityNode.getPathName(),
                                                powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                            }
                        }, () ->
                                addNode(connectivityNode.getPathName(),
                                        createFictitiousNode(connectivityNode.getPathName()))
                );
    }

    private Optional<GenericPowerTransformer> getPowerTransformer(String pathName) {
        if (substation != null) {
            return substation.getPowerTransformerByConnectivityNode(pathName);
        }
        return Optional.empty();
    }

    private void processConductingEquipment(GenericConductingEquipment conductingEquipment) {
        var terminals = conductingEquipment.getTerminals();
        var fullName = conductingEquipment.getFullName();
        var node = createSwitchNode(fullName);

        Node node1 = terminalToNode(terminals.get(0));
        Node node2 = null;
        var termNb = terminals.size();
        if (termNb == 1) {
            node2 = createLoad(fullName + "/Grounded");
        } else if (termNb == 2) {
            node2 = terminalToNode(terminals.get(1));
        }
        connectNode(node, node1);
        connectNode(node, node2);
    }

    private Node terminalToNode(GenericTerminal terminal) {
        var pathName = terminal.getConnectivityNode();
        if (pathName != null) {
            return getNodeByPath(pathName);
        }
        return createLoad(terminal.getCNodeName());
    }

    private SwitchNode createSwitchNode(String id) {
        SwitchNode sw = new SwitchNode(id, id, SwitchNode.SwitchKind.BREAKER.name(), false,
                getGraph(), SwitchNode.SwitchKind.BREAKER, false);
        getGraph().addNode(sw);
        return sw;
    }

    private void connectNode(Node node1, Node node2) {
        getGraph().addEdge(node1, node2);
    }

    private FictitiousNode createFictitiousNode(String id) {
        InternalNode fictitiousNode = new InternalNode(id, getGraph());
        getGraph().addNode(fictitiousNode);
        return fictitiousNode;
    }

    private FeederNode createLoad(String id) {
        FeederInjectionNode fn = FeederInjectionNode.createLoad(getGraph(), id, id);
        commonFeederSetting(fn, id, 0, null);
        return fn;
    }

    public Feeder2WTLegNode createFeeder2WTLegNode(String id, FeederWithSideNode.Side side, int order,
                                                   BusCell.Direction direction) {
        Feeder2WTLegNode f2WTe = Feeder2WTLegNode.create(getGraph(), id + "_" + side, id, id, side);
        commonFeederSetting(f2WTe, id, order, direction);
        return f2WTe;
    }

    public Feeder3WTLegNode createFeeder3WTLegNode(String id, FeederWithSideNode.Side side, int order,
                                                   BusCell.Direction direction) {
        Feeder3WTLegNode f3WTe = Feeder3WTLegNode.createForSubstationDiagram(getGraph(), id + "_" + side, id, id, side);
        commonFeederSetting(f3WTe, id + side.getIntValue(), order, direction);
        return f3WTe;
    }

    private void commonFeederSetting(FeederNode node, String id, int order, BusCell.Direction direction) {
        node.setLabel(id);
        getGraph().addNode(node);

        if (direction != null) {
            node.setOrder(order);
            node.setDirection(direction);
        }
    }
}
