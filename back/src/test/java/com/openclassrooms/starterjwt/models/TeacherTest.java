package com.openclassrooms.starterjwt.models;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherTest {

    @Test
    void builder_shouldCreateTeacherWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Testfirstname")
                .lastName("Testlasttname")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("Testfirstname");
        assertThat(teacher.getLastName()).isEqualTo("Testlasttname");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equalsAndHashCode_shouldWorkBasedOnIdOnly() {
        Teacher t1 = Teacher.builder().id(1L).firstName("Testfirstname").lastName("Testlasttname").build();
        Teacher t2 = Teacher.builder().id(1L).firstName("TestAnotherFirstName").lastName("TestAnotherLastName").build();
        Teacher t3 = Teacher.builder().id(2L).firstName("Testfirstname").lastName("Testlasttname").build();

        assertThat(t1).isEqualTo(t2);
        assertThat(t1).hasSameHashCodeAs(t2);
        assertThat(t1).isNotEqualTo(t3);
    }

    @Test
    void equalsAndHashCode_shouldHandleAllComparisonBranches() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        assertThat(teacher.equals(teacher)).isTrue();

        assertThat(teacher.equals(null)).isFalse();

        assertThat(teacher.equals("teacher string")).isFalse();


        Teacher teacher2 = new Teacher();
        teacher2.setId(null);
        assertThat(teacher.equals(teacher2)).isFalse();

        teacher.setId(null);
        assertThat(teacher.equals(teacher2)).isTrue();

        teacher.setId(1L);
        teacher2.setId(2L);
        assertThat(teacher.equals(teacher2)).isFalse();

        assertThat(teacher.hashCode()).isNotZero();
    }

    @Test
    void chainSetters_shouldWorkCorrectly() {
        Teacher teacher = new Teacher()
                .setFirstName("Testfirstname")
                .setLastName("Testflastname");

        assertThat(teacher.getFirstName()).isEqualTo("Testfirstname");
        assertThat(teacher.getLastName()).isEqualTo("Testflastname");
    }

    @Test
    void toString_shouldContainImportantFields() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Testfirstname")
                .lastName("Testlastname")
                .build();

        String result = teacher.toString();
        assertThat(result).contains("Testfirstname", "Testlastname", "id=1");
    }
}