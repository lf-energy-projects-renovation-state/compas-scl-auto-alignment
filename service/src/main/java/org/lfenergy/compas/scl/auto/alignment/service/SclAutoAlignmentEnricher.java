// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSubstation;
import org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevel;

import java.util.concurrent.atomic.AtomicLong;

public class SclAutoAlignmentEnricher {
    private GenericSCL scl;
    private String jsonGraphInfo;

    public SclAutoAlignmentEnricher(GenericSCL scl, String jsonGraphInfo) {
        this.scl = scl;
        this.jsonGraphInfo = jsonGraphInfo;
    }

    public void enrich() {
        var jsonSubstation = JsonParser.parseString(jsonGraphInfo).getAsJsonObject();
        var substationName = jsonSubstation.get("substationId").getAsString();
        var sclSubstation = scl.getSubstation(substationName);
        sclSubstation.ifPresent(substation -> {
            if (jsonSubstation.has("voltageLevels")) {
                jsonSubstation.getAsJsonArray("voltageLevels")
                        .forEach(jsonVoltageLevel -> enrichVoltageLevel(substation, jsonVoltageLevel.getAsJsonObject()));
            }
        });
    }

    private void enrichVoltageLevel(GenericSubstation substation, JsonObject jsonVoltageLevel) {
        var voltageLevelName = jsonVoltageLevel.get("voltageLevelInfos").getAsJsonObject().get("id").getAsString();
        var sclVoltageLevel = substation.getVoltageLevel(voltageLevelName);
        sclVoltageLevel.ifPresent(voltageLevel -> {
            voltageLevel.setXYCoordinates(getCoordinate(jsonVoltageLevel, "x"),
                    getCoordinate(jsonVoltageLevel, "y"));

            if (jsonVoltageLevel.has("nodes")) {
                jsonVoltageLevel.getAsJsonArray("nodes")
                        .forEach(jsonNode -> {
                            var type = jsonNode.getAsJsonObject().get("type").getAsString();
                            if ("BUS".equals(type)) {
                                enrichBusbar(voltageLevel, jsonNode.getAsJsonObject());
                            } else {
                                enrichConductingEquipment(voltageLevel, jsonNode.getAsJsonObject());
                            }
                        });
            }

            AtomicLong bayXCoordinate = new AtomicLong(1);
            voltageLevel.getBays()
                    .stream()
                    .filter(bay -> !bay.isBusbar())
                    .forEach(bay -> bay.setXYCoordinates(bayXCoordinate.getAndIncrement() * 10, 1));
        });
    }

    private void enrichBusbar(GenericVoltageLevel voltageLevel, JsonObject jsonBusbar) {
        var name = jsonBusbar.get("id").getAsString();
        var sclBusbar = voltageLevel.getBusbar(name);
        sclBusbar.ifPresent(busbar -> {
            busbar.setXYCoordinates(getCoordinate(jsonBusbar, "x"), getCoordinate(jsonBusbar, "y"));
        });
    }

    private void enrichConductingEquipment(GenericVoltageLevel voltageLevel, JsonObject jsonCoductingEquipment) {
        var ceName = jsonCoductingEquipment.get("id").getAsString();
        var sclConductingEquipment = voltageLevel.getConductingEquipment(ceName);
        sclConductingEquipment.ifPresent(conductingEquipment -> {
            conductingEquipment.setXYCoordinates(getCoordinate(jsonCoductingEquipment, "x"),
                    getCoordinate(jsonCoductingEquipment, "y"));
        });
    }

    private long getCoordinate(JsonObject jsonObject, String fieldName) {
        var coordinate = jsonObject.get(fieldName).getAsLong();
        return Math.max(coordinate / 10, 1);
    }
}
