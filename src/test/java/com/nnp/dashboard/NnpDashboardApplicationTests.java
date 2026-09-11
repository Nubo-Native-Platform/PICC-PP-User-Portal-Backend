package com.nnp.dashboard;

import com.nnp.dashboard.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic component and configuration unit tests for PICC-PP-User-Portal-Backend.
 */
class NnpDashboardApplicationTests {

    @Test
    @DisplayName("OpenAPI configuration bean initializes properly with metadata and tags")
    void testOpenApiConfiguration() {
        OpenApiConfig config = new OpenApiConfig();
        ReflectionTestUtils.setField(config, "serverPort", "8080");

        OpenAPI openApi = config.customOpenAPI();
        assertNotNull(openApi);
        assertNotNull(openApi.getInfo());
        assertEquals("PICC-PP-User-Portal-Backend REST API", openApi.getInfo().getTitle());
        assertNotNull(openApi.getInfo().getLicense());
        assertEquals("Apache License 2.0", openApi.getInfo().getLicense().getName());
        assertFalse(openApi.getServers().isEmpty());
        assertFalse(openApi.getTags().isEmpty());
    }

    @Test
    @DisplayName("Application provides a properly initialized ModelMapper bean")
    void testModelMapperBean() {
        NnpDashboardApplication app = new NnpDashboardApplication();
        ModelMapper mapper = app.getModelMapper();
        assertNotNull(mapper);
    }
}
