package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {


    private Date toDate(LocalDateTime localDatetime) {
        return Date.from(localDatetime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void builder_shouldCreateSessionWithExpectedValues() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("test firstname")
                .lastName("test lastname")
                .build();

        List<User> users = new ArrayList<>();

        Session session = Session.builder()
                .id(1L)
                .name("test session")
                .date(toDate(LocalDateTime.now().plusDays(1)))
                .description("test session description")
                .teacher(teacher)
                .users(users)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("test session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).isEmpty();
        assertThat(session.getDescription()).contains("test session description");
    }

    @Test
    void equalsAndHashCode_shouldDependOnId() {
        Session s1 = Session.builder().id(1L).name("Session A").build();
        Session s2 = Session.builder().id(1L).name("Session B").build();
        Session s3 = Session.builder().id(2L).name("Session A").build();

        assertThat(s1).isEqualTo(s2);
        assertThat(s1).hasSameHashCodeAs(s2);
        assertThat(s1).isNotEqualTo(s3);
    }

    @Test
    void equalsAndHashCode_shouldHandleAllComparisonBranches() {
        Session s1 = new Session();
        s1.setId(1L);

        assertThat(s1.equals(s1)).isTrue();

        assertThat(s1.equals(null)).isFalse();

        assertThat(s1.equals("not a session")).isFalse();

        Session s2 = new Session();
        s2.setId(null);
        assertThat(s1.equals(s2)).isFalse();

        s1.setId(null);
        assertThat(s1.equals(s2)).isTrue();

        s1.setId(1L);
        s2.setId(2L);
        assertThat(s1.equals(s2)).isFalse();

        assertThat(s1.hashCode()).isNotZero();
    }

    @Test
    void toString_shouldContainFieldValues() {
        Session session = Session.builder()
                .id(1L)
                .name("test session name")
                .description("test session description")
                .build();

        String result = session.toString();

        assertThat(result).contains("test session name");
        assertThat(result).contains("test session description");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Session session = new Session();
        Teacher teacher = new Teacher();
        teacher.setId(10L);

        session.setId(100L);
        session.setName("test session name");
        session.setDescription("test session description");
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>());

        assertThat(session.getId()).isEqualTo(100L);
        assertThat(session.getTeacher().getId()).isEqualTo(10L);
        assertThat(session.getName()).isEqualTo("test session name");
    }

    @Test
    void chainMethods_shouldWorkFluently() {
        Session session = new Session()
                .setName("testSessionName")
                .setDescription("test session description");

        assertThat(session.getName()).isEqualTo("testSessionName");
        assertThat(session.getDescription()).isEqualTo("test session description");
    }

}