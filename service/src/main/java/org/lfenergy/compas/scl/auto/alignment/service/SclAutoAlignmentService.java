// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.RawGraphBuilder;
import com.powsybl.sld.layout.HorizontalSubstationLayoutFactory;
import com.powsybl.sld.layout.LayoutParameters;
import com.powsybl.sld.layout.PositionVoltageLevelLayoutFactory;
import com.powsybl.sld.library.ConvergenceComponentLibrary;
import com.powsybl.sld.model.SubstationGraph;
import com.powsybl.sld.svg.DefaultDiagramStyleProvider;
import com.powsybl.sld.svg.DefaultSVGWriter;
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
import java.util.Optional;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.common.CommonUtil.cleanSXYDeclarationAndAttributes;
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
        var scl = readSCL(sclData);
        var substationBuilder = createSubstationBuilder(scl.getSubstation(substationName), substationName);

        // Create the JSON With all X/Y Coordinate information.
        var jsonGraphInfo = createJson(substationBuilder);
        // Use that JSON to enrich the passed SCL XML with X/Y Coordinates.
        var enricher = new SclAutoAlignmentEnricher(scl, jsonGraphInfo);
        enricher.enrich();

        // Add an extra History Element to show there was a change.
        scl.getOrCreateHeader().addHistoryItem(who, "Add or replaced the X/Y Coordinates in the SCL File.");

        return converter.convertToString(scl.getElement());
    }

    public String getSVG(String sclData, String substationName) {
        var scl = readSCL(sclData);
        var substationBuilder = createSubstationBuilder(scl.getSubstation(substationName), substationName);

        return createSVG(substationBuilder);
    }

    GenericSCL readSCL(String sclData) {
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

    RawGraphBuilder.SubstationBuilder createSubstationBuilder(Optional<GenericSubstation> substation,
                                                              String substationName) {
        return substation.map(value -> {
            var builder = new SclAutoAlignmentGraphBuilder(value);
            return builder.getSubstationBuilder();
        }).orElseThrow(() -> {
            throw new SclAutoAlignmentException(SUBSTATION_NOT_FOUND_ERROR_CODE,
                    "No substation found with name '" + substationName + "'.");
        });
    }

    String createJson(RawGraphBuilder.SubstationBuilder substationBuilder) {
        var graph = substationBuilder.getSsGraph();
        var layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        graph.writeJson(writer);
        return writer.toString();
    }


    String createSVG(RawGraphBuilder.SubstationBuilder substationBuilder) {
        var graph = substationBuilder.getSsGraph();
        var layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        var svgWriter = new DefaultSVGWriter(new ConvergenceComponentLibrary(), layoutParameters);
        svgWriter.write("", graph, new SclAutoAlignmentDiagramLabelProvider(graph), new DefaultDiagramStyleProvider(), writer);
        return writer.toString();
    }

    private LayoutParameters getLayoutParameters() {
        return new LayoutParameters()
                .setAdaptCellHeightToContent(true)
                .setShowInternalNodes(true)
                .setCssLocation(LayoutParameters.CssLocation.INSERTED_IN_SVG);
    }

    private void configureLayout(SubstationGraph graph, LayoutParameters layoutParameters) {
        new HorizontalSubstationLayoutFactory().create(graph,
                        new PositionVoltageLevelLayoutFactory()
                                .setFeederStacked(false)
                                .setHandleShunts(true))
                .run(layoutParameters);
    }
}
