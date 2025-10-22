package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


class SessionMapperTest {
    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionMapperImpl mapper;

    private Teacher teacher;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        teacher = new Teacher(1L, "Teacherlastname", "Treacherfirstname", null, null);
        user = new User(1L, "test@mail.com", "Userlastname", "Userfirstname", "password", false, null, null);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user);
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setName("Test Session");
        dto.setDate(new Date());
        dto.setDescription("Test session description");
        dto.setTeacher_id(1L);
        dto.setUsers(Arrays.asList(1L));

        Session session = mapper.toEntity(dto);

        assertThat(session.getName()).isEqualTo("Test Session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).contains(user);
    }

    @Test
    void toEntity_shouldMapDtoListToEntityList() {
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setName("Test Session");
        dto.setDate(new Date());
        dto.setDescription("Test session description");
        dto.setTeacher_id(1L);
        dto.setUsers(Arrays.asList(1L));
        List<SessionDto> dtos = Arrays.asList(dto);

        List<Session> sessions = mapper.toEntity(dtos);

        assertThat(sessions.get(0).getName()).isEqualTo("Test Session");
        assertThat(sessions.get(0).getTeacher()).isEqualTo(teacher);
        assertThat(sessions.get(0).getUsers()).contains(user);
    }

    @Test
    void toEntity_shouldMapDtoToNullWhenDtoIsNull() {


        Session session = mapper.toEntity((SessionDto) null);
        List<Session> sessions = mapper.toEntity((List<SessionDto>) null);

        assertThat(session).isNull();
        assertThat(sessions).isNull();

    }


    @Test
    void toDto_shouldMapEntityToDto() {
        Session session = Session.builder()
                .id(1L)
                .name("test session")
                .date(new Date())
                .description("test session description")
                .teacher(teacher)
                .users(Arrays.asList(user))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getTeacher_id()).isEqualTo(teacher.getId());
        assertThat(dto.getUsers()).containsExactly(user.getId());
        assertThat(dto.getName()).isEqualTo("test session");
    }

    @Test
    void toDto_shouldMapEntityListToDtoList() {
        Session session = Session.builder()
                .id(1L)
                .name("test session")
                .date(new Date())
                .description("test session description")
                .teacher(teacher)
                .users(Arrays.asList(user))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Session> sessions = Arrays.asList(session);

        List<SessionDto> dtos = mapper.toDto(sessions);

        assertThat(dtos.get(0).getTeacher_id()).isEqualTo(teacher.getId());
        assertThat(dtos.get(0).getUsers()).containsExactly(user.getId());
        assertThat(dtos.get(0).getName()).isEqualTo("test session");
    }

    @Test
    void toDto_shouldMapEntityToDtoAndSetTeacherNull() {
        Session session = Session.builder()
                .id(1L)
                .name("test session")
                .date(new Date())
                .description("test session description")
                .teacher(null)
                .users(Arrays.asList(user))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getTeacher_id()).isNull();
        assertThat(dto.getUsers()).containsExactly(user.getId());
        assertThat(dto.getName()).isEqualTo("test session");
    }

    @Test
    void toDto_shouldMapEntityToDtoAndSetTeacherNullWhenNoId() {
        Teacher teacherNoId = new Teacher();
        teacherNoId.setId(null);
        Session session = Session.builder()
                .id(1L)
                .name("test session")
                .date(new Date())
                .description("test session description")
                .teacher(teacherNoId)
                .users(Arrays.asList(user))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getTeacher_id()).isNull();
        assertThat(dto.getUsers()).containsExactly(user.getId());
        assertThat(dto.getName()).isEqualTo("test session");
    }

    @Test
    void toDto_shouldMapEntityToNullIfSessionIsNull() {

        SessionDto dto = mapper.toDto((Session) null);
        List<SessionDto> dtos = mapper.toDto((List<Session>) null);

        assertThat(dto).isNull();
        assertThat(dtos).isNull();
    }

}