package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session createSession(Long id) {
        Session session = new Session();
        session.setId(id);
        session.setName("test session");
        session.setDate(new Date());
        session.setDescription("description test");
        Teacher teacher = new Teacher();
        teacher.setId(3L);
        session.setTeacher(teacher);
        User user = new User();
        user.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        session.setUsers(users);
        return session;
    }
    private SessionDto createSessionDTO(Long id) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(id);
        sessionDto.setName("test session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("description test");
        sessionDto.setTeacher_id(3L);
        List<Long> users = new ArrayList<Long>();
        users.add(1L);
        users.add(2L);
        sessionDto.setUsers(users);
        return sessionDto;
    }

    @Test
    void findById_shouldReturnOk_whenSessionExists() throws Exception {
        Session session = createSession(1L);
        SessionDto dto = createSessionDTO(1L);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test session"));
    }

    @Test
    void findById_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdInvalid() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldReturnListOfSessions() throws Exception {
        List<Session> sessions = new ArrayList<>();
        sessions.add(createSession(1L));
        sessions.add(createSession(2L));
        List<SessionDto> dtos = new ArrayList<>();
        dtos.add(createSessionDTO(1L));
        dtos.add(createSessionDTO(2L));

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(dtos);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void create_shouldReturnOk_whenSessionCreated() throws Exception {
        SessionDto requestDto = createSessionDTO(null);
        Session entity = createSession(null);
        Session savedEntity = createSession(3L);

        SessionDto responseDto = createSessionDTO(3L);
        responseDto.setName("new test session");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(entity);
        when(sessionService.create(entity)).thenReturn(savedEntity);
        when(sessionMapper.toDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("new test session"));
    }

    @Test
    void update_shouldReturnOk_whenSessionUpdated() throws Exception {
        SessionDto dto = createSessionDTO(1L);

        Session entity = createSession(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(entity);
        when(sessionService.update(eq(1L), eq(entity))).thenReturn(entity);
        when(sessionMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test session"));
    }

    @Test
    void delete_shouldReturnOk_whenSessionExists() throws Exception {
        Session session = createSession(1L);
        when(sessionService.getById(1L)).thenReturn(session);
        Mockito.doNothing().when(sessionService).delete(1L);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNotFound_whenSessionMissing() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldReturnOk_whenCalledWithValidIds() throws Exception {
        Mockito.doNothing().when(sessionService).participate(1L, 2L);

        mockMvc.perform(post("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    @Test
    void participate_shouldReturnBadRequest_whenIdsInvalid() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noLongerParticipate_shouldReturnOk_whenCalledWithValidIds() throws Exception {
        Mockito.doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        mockMvc.perform(delete("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }


}
