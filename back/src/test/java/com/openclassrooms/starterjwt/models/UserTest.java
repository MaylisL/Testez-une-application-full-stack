package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void builder_shouldCreateUserWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .email("testuser@mail.com")
                .firstName("Tester")
                .lastName("TestLastname")
                .password("securePass123")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("testuser@mail.com");
        assertThat(user.getFirstName()).isEqualTo("Tester");
        assertThat(user.getLastName()).isEqualTo("TestLastname");
        assertThat(user.getPassword()).isEqualTo("securePass123");
        assertThat(user.isAdmin()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void builder_shouldThrowNullPointer_whenRequiredFieldIsNull() {
        assertThatThrownBy(() -> User.builder()
                .id(1L)
                .email(null)
                .firstName("Tester")
                .lastName("TestLastname")
                .password("password")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email");

        assertThatThrownBy(() -> User.builder()
                .id(1L)
                .email("test@mail.com")
                .firstName(null)
                .lastName("TestLastname")
                .password("password")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("firstName");

        assertThatThrownBy(() -> User.builder()
                .id(1L)
                .email("test@mail.com")
                .firstName("firstname")
                .lastName(null)
                .password("password")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("lastName");

        assertThatThrownBy(() -> User.builder()
                .id(1L)
                .email("test@mail.com")
                .firstName("Tester")
                .lastName("TestLastname")
                .password(null)
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("password");
    }

    @Test
    void requiredArgsConstructor_shouldSetNonNullFields() {
        User user = new User(
                "test@mail.com",
                "TestLastname",
                "Tester",
                "password",
                false
        );

        assertThat(user.getEmail()).isEqualTo("test@mail.com");
        assertThat(user.getLastName()).isEqualTo("TestLastname");
        assertThat(user.getFirstName()).isEqualTo("Tester");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                1L,
                "test@mail.com",
                "TestLastname",
                "Tester",
                "password",
                false,
                now,
                now
        );

        assertThat(user.getEmail()).isEqualTo("test@mail.com");
        assertThat(user.getLastName()).isEqualTo("TestLastname");
        assertThat(user.getFirstName()).isEqualTo("Tester");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void allArgsConstructor_shouldThrowNullPointer_whenRequiredFieldIsNull() {
        assertThatThrownBy(() -> new User(
                1L,
                null,
                "lastname",
                "firstname",
                "password",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email");

        assertThatThrownBy(() -> new User(
                1L,
                "test@mail.com",
                null,
                "firstname",
                "password",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("lastName");

        assertThatThrownBy(() -> new User(
                1L,
                "test@mail.com",
                "lastname",
                null,
                "password",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("firstName");

        assertThatThrownBy(() -> new User(
                1L,
                "test@mail.com",
                "lastname",
                "firstname",
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("password");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        User user = new User();

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.isAdmin()).isFalse();
    }



    @Test
    void equalsAndHashCode_shouldDependOnlyOnId() {
        User u1 = User.builder().id(1L).email("test1@mail.com").firstName("testname1").lastName("testlastname1").password("password1").admin(false).build();
        User u2 = User.builder().id(1L).email("test2@mail.com").firstName("testname2").lastName("testlastname2").password("password2").admin(true).build();
        User u3 = User.builder().id(2L).email("test3@mail.com").firstName("testname3").lastName("testlastname3").password("password3").admin(false).build();

        assertThat(u1).isEqualTo(u2);
        assertThat(u1).hasSameHashCodeAs(u2);
        assertThat(u1).isNotEqualTo(u3);
    }

    @Test
    void equalsAndHashCode_shouldHandleAllComparisonBranches() {
        User user1 = new User();
        user1.setId(1L);

        assertThat(user1.equals(user1)).isTrue();

        assertThat(user1.equals(null)).isFalse();

        assertThat(user1.equals("not a user")).isFalse();


        User user2 = new User();
        user2.setId(null);
        assertThat(user1.equals(user2)).isFalse();


        user1.setId(null);
        assertThat(user1.equals(user2)).isTrue();

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }


    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        User user = new User();
        user.setId(11L);
        user.setEmail("test@mail.com");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setPassword("testpassword");
        user.setAdmin(false);

        assertThat(user.getId()).isEqualTo(11L);
        assertThat(user.getEmail()).isEqualTo("test@mail.com");
        assertThat(user.getFirstName()).isEqualTo("testFirstName");
        assertThat(user.getLastName()).isEqualTo("testLastName");
        assertThat(user.getPassword()).isEqualTo("testpassword");
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void chainSetters_shouldWorkCorrectly() {
        User user = new User()
                .setEmail("user@example.com")
                .setFirstName("Alice")
                .setLastName("Brown")
                .setPassword("secret")
                .setAdmin(true);

        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getFirstName()).isEqualTo("Alice");
        assertThat(user.getLastName()).isEqualTo("Brown");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void toString_shouldContainKeyFields() {
        User user = User.builder()
                .id(10L)
                .email("user@example.com")
                .firstName("John")
                .lastName("TestLastname")
                .password("secret")
                .admin(false)
                .build();

        String result = user.toString();
        assertThat(result).contains("user@example.com", "John", "TestLastname", "id=10");
    }
}