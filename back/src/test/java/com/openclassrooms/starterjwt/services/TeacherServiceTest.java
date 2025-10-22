package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeacherServiceTest {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher createTeacher(String firstName, String lastName) {
        Teacher teacher = Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        return teacherRepository.save(teacher);
    }

    @Test
    void findAll_shouldReturnAllTeachers() {
        createTeacher("Teacherfirstname1", "Teacherlastname1");
        createTeacher("Teacherfirstname2", "Teacherlastname2");

        List<Teacher> teachers = teacherService.findAll();

        assertThat(teachers).hasSize(2);
        assertThat(teachers)
                .extracting(Teacher::getLastName)
                .containsExactlyInAnyOrder("Teacherlastname1", "Teacherlastname2");
    }

    @Test
    void findById_shouldReturnTeacher_whenExists() {
        Teacher saved = createTeacher("TeacherFirstname", "TeacherLastname");

        Teacher found = teacherService.findById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("TeacherFirstname");
        assertThat(found.getLastName()).isEqualTo("TeacherLastname");
    }

    @Test
    void findById_shouldReturnNull_whenTeacherDoesNotExist() {
        Teacher result = teacherService.findById(999L);

        assertThat(result).isNull();
    }
}