// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.builder;

import com.powsybl.sld.model.BaseGraph;
import com.powsybl.sld.model.Node;

import java.util.Map;

public abstract class AbstractGraphBuilder<G extends BaseGraph> {
    private G graph;
    private final Map<String, Node> path2Node;

    protected AbstractGraphBuilder(Map<String, Node> path2Node) {
        this.path2Node = path2Node;
    }

    public G getGraph() {
        return graph;
    }

    protected void setGraph(G graph) {
        this.graph = graph;
    }

    protected void addNode(String path, Node node) {
        path2Node.put(path, node);
    }

    protected Node getNodeByPath(String path) {
        return path2Node.get(path);
    }

    protected Map<String, Node> getPath2Node() {
        return path2Node;
    }
}
