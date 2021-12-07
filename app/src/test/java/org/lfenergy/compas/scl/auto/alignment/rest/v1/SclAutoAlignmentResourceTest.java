package org.lfenergy.compas.scl.auto.alignment.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;
import org.lfenergy.compas.scl.auto.alignment.service.SclAutoAlignmentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(SclAutoAlignmentResource.class)
@TestSecurity(user = "test-user")
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = SclAutoAlignmentResourceTest.USERNAME)
})
class SclAutoAlignmentResourceTest {
    public static final String USERNAME = "Test User";
    public static final String SUBSTATION_NAME = "AA1";

    @InjectMock
    private SclAutoAlignmentService sclAutoAlignmentService;

    @Test
    void updateSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws IOException {
        var request = new SclAutoAlignRequest();
        request.setSubstationName(SUBSTATION_NAME);
        request.setSclData(readFile());

        var expectedResult = "SCL XML";
        when(sclAutoAlignmentService.updateSCL(any(), eq(SUBSTATION_NAME), eq(USERNAME))).thenReturn(expectedResult);

        var response = given()
                .contentType(ContentType.XML)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("saa", SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI));
        var scl = xmlPath.getString("saa:SclAutoAlignmentResponse.SclData");
        assertNotNull(scl);
        assertEquals(expectedResult, scl);
        verify(sclAutoAlignmentService, times(1)).updateSCL(any(), eq(SUBSTATION_NAME), eq(USERNAME));
    }

    @Test
    void getSVG_WhenCalled_ThenExpectedResponseIsRetrieved() throws IOException {
        var request = new SclAutoAlignRequest();
        request.setSubstationName(SUBSTATION_NAME);
        request.setSclData(readFile());

        var expectedResult = "SVG DATA";
        when(sclAutoAlignmentService.getSVG(any(), eq(SUBSTATION_NAME))).thenReturn(expectedResult);

        var response = given()
                .contentType(ContentType.XML)
                .body(request)
                .when()
                .post("/svg")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var svg = response.asString();
        assertNotNull(svg);
        assertEquals(expectedResult, svg);
        verify(sclAutoAlignmentService, times(1)).getSVG(any(), eq(SUBSTATION_NAME));
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/scl/scl-1.scd"));
        var path = Paths.get(resource.getPath());
        return String.join("\n", Files.readAllLines(path));
    }

}