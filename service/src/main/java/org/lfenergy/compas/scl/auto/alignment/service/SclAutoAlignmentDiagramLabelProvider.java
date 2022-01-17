// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.model.FeederNode;
import com.powsybl.sld.model.Node;
import com.powsybl.sld.model.SubstationGraph;
import com.powsybl.sld.svg.DiagramLabelProvider;
import com.powsybl.sld.svg.FeederInfo;
import com.powsybl.sld.svg.LabelPosition;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SclAutoAlignmentDiagramLabelProvider implements DiagramLabelProvider {
    private final Map<Node, List<NodeLabel>> busLabels = new HashMap<>();

    public SclAutoAlignmentDiagramLabelProvider(SubstationGraph graph) {
        graph.getVoltageLevels().forEach(voltageLevelGraph ->
                voltageLevelGraph.getNodes().forEach(this::addNode)
        );
        graph.getMultiTermNodes().forEach(this::addNode);
    }

    private void addNode(Node node) {
        LabelPosition labelPosition = new LabelPosition("default", 0, -5, true, 0);
        List<NodeLabel> labels = new ArrayList<>();
        labels.add(new NodeLabel(stripLabel(node.getId()), labelPosition, null));
        busLabels.put(node, labels);
    }

    @Override
    public List<FeederInfo> getFeederInfos(FeederNode node) {
        return Arrays.asList(new FeederInfo(ComponentTypeName.ARROW_ACTIVE, Direction.OUT, "", "", null),
                new FeederInfo(ComponentTypeName.ARROW_REACTIVE, Direction.IN, "", "", null));
    }

    @Override
    public List<NodeLabel> getNodeLabels(com.powsybl.sld.model.Node node) {
        return busLabels.get(node);
    }

    @Override
    public List<NodeDecorator> getNodeDecorators(com.powsybl.sld.model.Node node) {
        return new ArrayList<>();
    }

    String stripLabel(String id) {
        String label = id;
        if (StringUtils.isNotBlank(label) && label.lastIndexOf("/") >= 0) {
            label = label.substring(label.lastIndexOf("/") + 1);
        }
        return label;
    }
}