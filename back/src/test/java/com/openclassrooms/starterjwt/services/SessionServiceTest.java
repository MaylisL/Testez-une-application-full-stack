package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(Long id, String email, String firstName, String lastName, boolean admin) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("password123")
                .admin(admin)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Session createSession(Long id, String name, Date date, Teacher teacher) {
        return Session.builder()
                .id(id)
                .name(name)
                .date(date)
                .description("Test session description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
    }

    private Teacher createTeacher(Long id, String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        return Teacher.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }


    @Test
    void create_shouldSaveSession() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Date date = toDate(LocalDateTime.now().plusDays(1));
        Session session = createSession(null, "test session name", date, teacher);

        Session saved = sessionService.create(session);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("test session name");
    }

    @Test
    void findAll_shouldReturnAllSessions() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        sessionService.create(createSession(null, "test session 1", toDate(LocalDateTime.now().plusDays(1)), teacher));
        sessionService.create(createSession(null, "test session 2", toDate(LocalDateTime.now().plusDays(2)), teacher));

        List<Session> sessions = sessionService.findAll();

        assertThat(sessions).hasSize(2);
        assertThat(sessions).extracting(Session::getName).containsExactlyInAnyOrder("test session 1", "test session 2");
    }

    @Test
    void getById_shouldReturnSession_whenExists() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session name", toDate(LocalDateTime.now().plusDays(1)), teacher));

        Session found = sessionService.getById(session.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("test session name");
    }

    @Test
    void getById_shouldReturnNull_whenSessionDoesNotExist() {
        Session found = sessionService.getById(999L);

        assertThat(found).isNull();
    }

    @Test
    void update_shouldSaveSession() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Date date = toDate(LocalDateTime.now().plusDays(1));
        Session createdSession = sessionService.create(createSession(null, "test session", date, teacher));

        createdSession.setName("test session updated");

        Session updatedSession = sessionService.update(createdSession.getId(), createdSession);

        assertThat(updatedSession.getId()).isNotNull();
        assertThat(updatedSession.getId()).isEqualTo(createdSession.getId());
        assertThat(updatedSession.getName()).isEqualTo("test session updated");
    }

    @Test
    void delete_shouldRemoveSession() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session", toDate(LocalDateTime.now().plusDays(1)), teacher));

        sessionService.delete(session.getId());

        Session found = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void participate_shouldAddUserToSession() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session", toDate(LocalDateTime.now().plusDays(1)), teacher));
        User user = userRepository.save(createUser(null, "test@mail.com", "Userfirstname", "Userlastname", false));

        sessionService.participate(session.getId(), user.getId());

        Session updated = sessionService.getById(session.getId());
        assertThat(updated.getUsers()).contains(user);
    }

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session", toDate(LocalDateTime.now().plusDays(1)), teacher));
        User user = userRepository.save(createUser(null, "test@mail.com", "Userfirstname", "Userlastname", false));
        sessionService.participate(session.getId(), user.getId());

        sessionService.noLongerParticipate(session.getId(), user.getId());

        Session updated = sessionService.getById(session.getId());
        assertThat(updated.getUsers()).doesNotContain(user);
    }

    @Test
    void participate_shouldThrow_whenUserAlreadyParticipates() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session", toDate(LocalDateTime.now().plusDays(1)), teacher));
        User user = userRepository.save(createUser(null, "test@mail.com", "Userfirstname", "Userlastname", false));
        sessionService.participate(session.getId(), user.getId());

        assertThatThrownBy(() -> sessionService.participate(session.getId(), user.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void noLongerParticipate_shouldThrow_whenUserNotParticipating() {
        Teacher teacher = teacherRepository.save(createTeacher(null, "Testfirstname", "Testlastname"));
        Session session = sessionService.create(createSession(null, "test session", toDate(LocalDateTime.now().plusDays(1)), teacher));
        User user = userRepository.save(createUser(null, "test@mail.com", "Userfirstname", "Userlastname",false));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(session.getId(), user.getId()))
                .isInstanceOf(BadRequestException.class);
    }
    @Test
    void participate_shouldThrowNotFound_whenSessionDoesNotExist() {
        User user = userRepository.save(createUser(null, "test@mail.com", "Testfirstname", "Testlastname", false));

        assertThatThrownBy(() -> sessionService.participate(999L, user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_shouldThrowNotFound_whenUserDoesNotExist() {
        Session session = sessionRepository.save(createSession(null, "test session name", toDate(LocalDateTime.now().plusDays(1)), null));

        assertThatThrownBy(() -> sessionService.participate(session.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFound_whenSessionDoesNotExist() {
        User user = userRepository.save(createUser(null, "test@mail.com", "Testfirstname", "Testlastname", false));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(999L, user.getId()))
                .isInstanceOf(NotFoundException.class);
    }


}