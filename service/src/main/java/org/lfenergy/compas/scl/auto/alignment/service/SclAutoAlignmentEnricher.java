// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.lfenergy.compas.scl.auto.alignment.common.CommonUtil.cleanSXYDeclarationAndAttributes;

public class SclAutoAlignmentEnricher {
    private final GenericSCL scl;
    private final String jsonGraphInfo;

    public SclAutoAlignmentEnricher(GenericSCL scl, String jsonGraphInfo) {
        this.scl = scl;
        this.jsonGraphInfo = jsonGraphInfo;
    }

    public void enrich() {
        var jsonSubstation = JsonParser.parseString(jsonGraphInfo).getAsJsonObject();
        var substationName = jsonSubstation.get("substationId").getAsString();
        var sclSubstation = scl.getSubstation(substationName);
        sclSubstation.ifPresent(substation -> {
            // First we will remove all old information from this Substation.
            cleanSXYDeclarationAndAttributes(substation.getElement());

            // Next process the VoltageLevels.
            if (jsonSubstation.has("voltageLevels")) {
                JsonArray jsonNodes = jsonSubstation.getAsJsonArray("voltageLevels");
                substation.getVoltageLevels()
                        .forEach(voltageLevel -> enrichVoltageLevel(jsonNodes, voltageLevel));
            }

            if (jsonSubstation.has("multitermNodes")) {
                JsonArray jsonNodes = jsonSubstation.getAsJsonArray("multitermNodes");
                substation.getPowerTransformers()
                        .forEach(powerTransformer -> enrichPowerTransformer(jsonNodes, powerTransformer));
            }
        });
    }

    private void enrichPowerTransformer(JsonArray jsonNodes, GenericPowerTransformer powerTransformer) {
        var jsonObject = findNode(jsonNodes, powerTransformer.getFullName());
        jsonObject.ifPresent(jsonPowerTransformer ->
                powerTransformer.setXYCoordinates(
                        getCoordinate(jsonPowerTransformer, "x"),
                        getCoordinate(jsonPowerTransformer, "y")));
    }

    private void enrichVoltageLevel(JsonArray jsonNodes, GenericVoltageLevel voltageLevel) {
        var jsonObject = findVoltageLevelNode(jsonNodes, voltageLevel.getFullName());
        jsonObject.ifPresent(jsonVoltageLevel -> {
            voltageLevel.setXYCoordinates(
                    getCoordinate(jsonVoltageLevel, "x"),
                    getCoordinate(jsonVoltageLevel, "y"));

            if (jsonVoltageLevel.has("nodes")) {
                JsonArray jsonSubNodes = jsonVoltageLevel.getAsJsonArray("nodes");
                voltageLevel.getBays()
                        .forEach(bay -> {
                            if (bay.isBusbar()) {
                                enrichBusbar(jsonSubNodes, bay);
                            } else {
                                enrichBay(jsonSubNodes, bay);
                            }
                        });
            }
        });
    }

    private Optional<JsonObject> findVoltageLevelNode(JsonArray jsonNodes, String fullName) {
        return StreamSupport.stream(jsonNodes.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> fullName.equals(jsonObject.get("voltageLevelInfos").getAsJsonObject().get("id").getAsString()))
                .findFirst();
    }

    private void enrichBusbar(JsonArray jsonNodes, GenericBay busbar) {
        var jsonObject = findNode(jsonNodes, busbar.getFullName());
        jsonObject.ifPresent(jsonBusbar ->
                busbar.setXYCoordinates(
                        getCoordinate(jsonBusbar, "x"),
                        getCoordinate(jsonBusbar, "y")));
    }

    private void enrichBay(JsonArray jsonNodes, GenericBay bay) {
        var xCoordinate = getMinimumCoordinate(jsonNodes, bay, "x");
        var yCoordinate = getMinimumCoordinate(jsonNodes, bay, "y");
        bay.setXYCoordinates(xCoordinate, yCoordinate);

        bay.getConductingEquipments()
                .forEach(conductingEquipment -> enrichConductingEquipment(jsonNodes, bay, conductingEquipment));
    }

    private void enrichConductingEquipment(JsonArray jsonNodes, GenericBay bay, GenericConductingEquipment conductingEquipment) {
        var jsonObject = findNode(jsonNodes, conductingEquipment.getFullName());
        jsonObject.ifPresent(jsonConductingEquipment ->
                conductingEquipment.setXYCoordinates(
                        getCoordinateForConductingEquipment(jsonConductingEquipment, bay, "x"),
                        getCoordinateForConductingEquipment(jsonConductingEquipment, bay, "y")));
    }

    private long getMinimumCoordinate(JsonArray jsonNodes, GenericBay bay, String fieldName) {
        return bay.getConductingEquipments()
                .stream()
                .map(conductingEquipment -> findNode(jsonNodes, conductingEquipment.getFullName()))
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .mapToLong(jsonObject -> getCoordinate(jsonObject, fieldName))
                .min()
                .orElse(1);
    }

    private Optional<JsonObject> findNode(JsonArray jsonNodes, String fullName) {
        return StreamSupport.stream(jsonNodes.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> fullName.equals(jsonObject.get("id").getAsString()))
                .findFirst();
    }

    private long getCoordinateForConductingEquipment(JsonObject jsonObject, GenericBay bay, String fieldName) {
        return Math.max(getCoordinate(jsonObject, fieldName) - bay.getCoordinate(fieldName) + 1, 1);
    }

    private long getCoordinate(JsonObject jsonObject, String fieldName) {
        var coordinate = jsonObject.get(fieldName).getAsLong();
        return Math.max(coordinate / 20, 1);
    }
}
