package com.gorkemuysal.eventBookingApplication.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;
import com.gorkemuysal.eventBookingApplication.event.service.impl.EventServiceImpl;

@WebMvcTest(EventController.class)
public class EventControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EventServiceImpl eventService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@WithMockUser(roles = "ADMIN")
	void create_shouldReturn201_whenAdminValidRequest() throws Exception {

		EventRequest request = new EventRequest("test title", "test descripton", "test vanue",
				LocalDateTime.of(2026, 6, 15, 10, 0), 100, BigDecimal.valueOf(250), EventStatus.DRAFT);

		EventResponse response = new EventResponse(1L, "test title", "test descripton", "test vanue",
				LocalDateTime.now(), 100, BigDecimal.valueOf(250), EventStatus.DRAFT, "gorkem@test.com");

		when(eventService.create(any(EventRequest.class))).thenReturn(response);

		// when + then
		mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.title").value("test title"))
				.andExpect(jsonPath("$.status").value("DRAFT"));

	}


    @Test
    @WithMockUser(roles = "USER") 
    void create_shouldReturn403_whenNotAdmin() throws Exception {
    	
        EventRequest request = new EventRequest(
                "test title", "test descripton", "test vanue",
                LocalDateTime.of(2026, 6, 15, 10, 0),
                100, BigDecimal.valueOf(250), EventStatus.DRAFT
        );

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(eventService, never()).create(any(EventRequest.class));
        
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenTitleIsBlank() throws Exception {
    	
        EventRequest invalidRequest = new EventRequest(
                "test title", "test descripton", "test vanue",
                LocalDateTime.of(2026, 6, 15, 10, 0),
                100, BigDecimal.valueOf(250), EventStatus.DRAFT
        );

        // when + then
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).create(any(EventRequest.class));
    }
	
	
}
