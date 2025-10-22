package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    private Teacher createTeacher(Long id, String firstName, String lastName) {
        return new Teacher(
                id,
                lastName,
                firstName,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now()
        );
    }

    private TeacherDto createTeacherDto(Long id, String firstName, String lastName) {
        return new TeacherDto(
                id,
                lastName,
                firstName,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now()
        );
    }

    @Test
    void findById_shouldReturnTeacher_whenFound() throws Exception {
        Teacher teacher = createTeacher(1L, "Teacherfirstname", "Teacherlastname");
        TeacherDto dto = createTeacherDto(1L, "Teacherfirstname", "Teacherlastname");

        Mockito.when(teacherService.findById(1L)).thenReturn(teacher);
        Mockito.when(teacherMapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Teacherfirstname"))
                .andExpect(jsonPath("$.lastName").value("Teacherlastname"));
    }

    @Test
    void findById_shouldReturnNotFound_whenTeacherDoesNotExist() throws Exception {
        Mockito.when(teacherService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsNotNumber() throws Exception {
        mockMvc.perform(get("/api/teacher/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldReturnListOfTeachers() throws Exception {
        List<Teacher> teachers = Arrays.asList(
                createTeacher(1L, "Teacherfirstname1", "Teacherlastname1"),
                createTeacher(2L, "Teacherfirstname2", "Teacherlastname2")
        );
        List<TeacherDto> dtos = Arrays.asList(
                createTeacherDto(1L, "Teacherfirstname1", "Teacherlastname1"),
                createTeacherDto(2L, "Teacherfirstname2", "Teacherlastname2")
        );

        Mockito.when(teacherService.findAll()).thenReturn(teachers);
        Mockito.when(teacherMapper.toDto(teachers)).thenReturn(dtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Teacherfirstname1"))
                .andExpect(jsonPath("$[0].lastName").value("Teacherlastname1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Teacherfirstname2"))
                .andExpect(jsonPath("$[1].lastName").value("Teacherlastname2"));
    }

}