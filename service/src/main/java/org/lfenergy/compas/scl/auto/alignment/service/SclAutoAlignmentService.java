// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.RawGraphBuilder;
import com.powsybl.sld.layout.LayoutParameters;
import com.powsybl.sld.layout.PositionVoltageLevelLayoutFactory;
import com.powsybl.sld.layout.VerticalSubstationLayoutFactory;
import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.library.ConvergenceComponentLibrary;
import com.powsybl.sld.model.FeederNode;
import com.powsybl.sld.model.SubstationGraph;
import com.powsybl.sld.svg.*;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSubstation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_SCL_ELEMENT_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.SUBSTATION_NOT_FOUND_ERROR_CODE;

@ApplicationScoped
public class SclAutoAlignmentService {
    private final ElementConverter converter;

    @Inject
    public SclAutoAlignmentService(ElementConverter converter) {
        this.converter = converter;
    }

    public String updateSCL(String sclData, String substationName, String who) {
        GenericSCL scl = readSCL(sclData);
        RawGraphBuilder.SubstationBuilder substationBuilder =
                createSubstationBuilder(scl.getSubstation(substationName), substationName);

        // Create the JSON With all X/Y Coordinate information.
        var jsonGraphInfo = createJson(substationBuilder);
        // Use that JSON to enrich the passed SCL XML with X/Y Coordinates.
        var enricher = new SclAutoAlignmentEnricher(scl, jsonGraphInfo);
        enricher.enrich();

        return converter.convertToString(scl.getElement());
    }

    public String getJson(String sclData, String substationName) {
        GenericSCL scl = readSCL(sclData);
        RawGraphBuilder.SubstationBuilder substationBuilder =
                createSubstationBuilder(scl.getSubstation(substationName), substationName);

        return createJson(substationBuilder);
    }

    public String getSVG(String sclData, String substationName) {
        GenericSCL scl = readSCL(sclData);
        RawGraphBuilder.SubstationBuilder substationBuilder =
                createSubstationBuilder(scl.getSubstation(substationName), substationName);

        return createSVG(substationBuilder);
    }

    private GenericSCL readSCL(String sclData) {
        // First we will cleanup existing X/Y Coordinates from the SCL XML.
        var cleanSclData = cleanSXYDeclarationAndAttributes(sclData);

        // Next convert the String to W3C Document/Element
        var sclElement = converter.convertToElement(new BufferedInputStream(
                new ByteArrayInputStream(cleanSclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
        if (sclElement == null) {
            throw new SclAutoAlignmentException(NO_SCL_ELEMENT_FOUND_ERROR_CODE, "No valid SCL found in the passed SCL Data.");
        }

        return new GenericSCL(sclElement);
    }

    static String cleanSXYDeclarationAndAttributes(String data) {
        // Find Prefix of Namespace.
        var pattern = Pattern.compile("xmlns:([A-Za-z0-9]*)=\\\"" + SCLXY_NS_URI + "\\\"");
        var matcher = pattern.matcher(data);

        if (matcher.find()) {
            var prefix = matcher.group(1);
            var replacementPattern = "xmlns:[A-Za-z0-9]*=\\\"" + SCLXY_NS_URI + "\\\"" + // Remove the namespace declaration.
                    "|" + // Combine the two regex patterns.
                    prefix + ":[A-Za-z]*=\\\"[A-Za-z0-9]*\\\" "; // Remove the attributes using that namespace.
            return data.replaceAll(replacementPattern, "");
        }
        return data;
    }

    private RawGraphBuilder.SubstationBuilder createSubstationBuilder(Optional<GenericSubstation> substation,
                                                                      String substationName) {
        return substation.map(value -> {
            var builder = new SclAutoAlignmentGraphBuilder(value);
            return builder.getSubstationBuilder();
        }).orElseThrow(() -> {
            throw new SclAutoAlignmentException(SUBSTATION_NOT_FOUND_ERROR_CODE,
                    "No substation found with name '" + substationName + "'.");
        });
    }

    private String createJson(RawGraphBuilder.SubstationBuilder substationBuilder) {
        var graph = substationBuilder.getSsGraph();

        LayoutParameters layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        graph.writeJson(writer);
        return writer.toString();
    }


    private String createSVG(RawGraphBuilder.SubstationBuilder substationBuilder) {
        var graph = substationBuilder.getSsGraph();

        LayoutParameters layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        DefaultSVGWriter svgWriter = new DefaultSVGWriter(new ConvergenceComponentLibrary(), layoutParameters);
        svgWriter.write("", graph, new RawDiagramLabelProvider(graph), new DefaultDiagramStyleProvider(), writer);
        return writer.toString();
    }

    private LayoutParameters getLayoutParameters() {
        return new LayoutParameters()
                .setAdaptCellHeightToContent(true)
                .setCssLocation(LayoutParameters.CssLocation.INSERTED_IN_SVG);
    }

    private void configureLayout(SubstationGraph graph, LayoutParameters layoutParameters) {
        new VerticalSubstationLayoutFactory().create(graph,
                        new PositionVoltageLevelLayoutFactory()
                                .setFeederStacked(false)
                                .setHandleShunts(true))
                .run(layoutParameters);
    }


    private static class RawDiagramLabelProvider implements DiagramLabelProvider {
        private final Map<com.powsybl.sld.model.Node, List<NodeLabel>> busLabels;

        public RawDiagramLabelProvider(SubstationGraph graph) {
            this.busLabels = new HashMap<>();
            LabelPosition labelPosition = new LabelPosition("default", 0, -5, true, 0);
            graph.getNodes().forEach(v ->
                    v.getNodes().forEach(n -> {
                        List<NodeLabel> labels = new ArrayList<>();
                        labels.add(new NodeLabel(n.getId(), labelPosition, null));
                        busLabels.put(n, labels);
                    })
            );
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
    }
}
