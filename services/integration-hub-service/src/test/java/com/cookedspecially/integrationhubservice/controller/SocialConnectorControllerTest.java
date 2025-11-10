package com.cookedspecially.integrationhubservice.controller;

import com.cookedspecially.integrationhubservice.domain.ConnectorStatus;
import com.cookedspecially.integrationhubservice.domain.ConnectorType;
import com.cookedspecially.integrationhubservice.dto.connector.CreateSocialConnectorDTO;
import com.cookedspecially.integrationhubservice.dto.connector.SocialConnectorDTO;
import com.cookedspecially.integrationhubservice.service.SocialConnectorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocialConnectorController.class)
class SocialConnectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SocialConnectorService service;

    @Test
    @WithMockUser
    void testCreateConnector() throws Exception {
        CreateSocialConnectorDTO createDTO = CreateSocialConnectorDTO.builder()
                .restaurantId(1L)
                .type(ConnectorType.FACEBOOK)
                .name("Facebook Connector")
                .description("Test connector")
                .build();

        SocialConnectorDTO responseDTO = SocialConnectorDTO.builder()
                .id(1L)
                .restaurantId(1L)
                .type(ConnectorType.FACEBOOK)
                .name("Facebook Connector")
                .enabled(true)
                .status(ConnectorStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(service.createConnector(any(CreateSocialConnectorDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/integrations/social-connectors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("FACEBOOK"));
    }

    @Test
    @WithMockUser
    void testGetConnectorsByRestaurant() throws Exception {
        SocialConnectorDTO connector1 = SocialConnectorDTO.builder()
                .id(1L)
                .restaurantId(1L)
                .type(ConnectorType.FACEBOOK)
                .name("Facebook")
                .enabled(true)
                .status(ConnectorStatus.ACTIVE)
                .build();

        SocialConnectorDTO connector2 = SocialConnectorDTO.builder()
                .id(2L)
                .restaurantId(1L)
                .type(ConnectorType.INSTAGRAM)
                .name("Instagram")
                .enabled(true)
                .status(ConnectorStatus.ACTIVE)
                .build();

        List<SocialConnectorDTO> connectors = Arrays.asList(connector1, connector2);

        when(service.getConnectorsByRestaurant(1L)).thenReturn(connectors);

        mockMvc.perform(get("/api/v1/integrations/social-connectors")
                        .param("restaurantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("FACEBOOK"))
                .andExpect(jsonPath("$[1].type").value("INSTAGRAM"));
    }
}
