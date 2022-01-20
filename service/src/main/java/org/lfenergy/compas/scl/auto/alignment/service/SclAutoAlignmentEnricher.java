// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lfenergy.compas.scl.auto.alignment.model.GenericBay;
import org.lfenergy.compas.scl.auto.alignment.model.GenericConductingEquipment;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSubstation;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
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
                jsonSubstation.getAsJsonArray("voltageLevels")
                        .forEach(jsonVoltageLevel -> enrichVoltageLevel(substation, jsonVoltageLevel.getAsJsonObject()));
            }

            AtomicLong pwtCoordinate = new AtomicLong(1);
            substation.getPowerTransformers().forEach(powerTransformer ->
                    powerTransformer.setXYCoordinates(pwtCoordinate.get(), pwtCoordinate.getAndIncrement()));
        });
    }

    private void enrichVoltageLevel(GenericSubstation substation, JsonObject jsonVoltageLevel) {
        var voltageLevelFullName = jsonVoltageLevel.get("voltageLevelInfos").getAsJsonObject().get("id").getAsString();
        var sclVoltageLevel = substation.getVoltageLevelByFullName(voltageLevelFullName);
        sclVoltageLevel.ifPresent(voltageLevel -> {
            voltageLevel.setXYCoordinates(getCoordinate(jsonVoltageLevel, "x"),
                    getCoordinate(jsonVoltageLevel, "y"));

            if (jsonVoltageLevel.has("nodes")) {
                JsonArray jsonNodes = jsonVoltageLevel.getAsJsonArray("nodes");
                voltageLevel.getBays()
                        .forEach(bay -> {
                            if (bay.isBusbar()) {
                                enrichBusbar(jsonNodes, bay);
                            } else {
                                enrichBay(jsonNodes, bay);
                            }
                        });
            }
        });
    }

    private void enrichBusbar(JsonArray jsonNodes, GenericBay busbar) {
        var jsonObject = findNode(jsonNodes, busbar.getFullName());
        jsonObject.ifPresent(jsonBusbar ->
                busbar.setXYCoordinates(getCoordinate(jsonBusbar, "x"), getCoordinate(jsonBusbar, "y")));
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

    private Optional<JsonObject> findNode(JsonArray jsonNodes, String fullname) {
        return StreamSupport.stream(jsonNodes.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> fullname.equals(jsonObject.get("id").getAsString()))
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
