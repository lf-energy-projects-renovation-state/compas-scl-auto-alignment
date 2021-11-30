// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.rest.v1;

import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.auto.alignment.rest.UserInfoProperties;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclRequest;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclResponse;
import org.lfenergy.compas.scl.auto.alignment.service.SclAutoAlignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Authenticated
@RequestScoped
@Path("/auto/alignment/v1")
public class SclAutoAlignmentResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SclAutoAlignmentResource.class);

    private SclAutoAlignmentService sclAutoAlignmentService;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    public SclAutoAlignmentResource(SclAutoAlignmentService compasCimMappingService) {
        this.sclAutoAlignmentService = compasCimMappingService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public SclResponse updateSCL(@Valid SclRequest request) {
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new SclResponse();
        response.setSclData(sclAutoAlignmentService.updateSCL(request.getSclData(), request.getSubstationName(), who));
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    public String getJSON(@Valid SclRequest request) {
        return sclAutoAlignmentService.getJson(request.getSclData(), request.getSubstationName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_SVG_XML)
    @Path("/svg")
    public String getSVG(@Valid SclRequest request) {
        return sclAutoAlignmentService.getSVG(request.getSclData(), request.getSubstationName());
    }
}