package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void builder_shouldCreateUserDetailsCorrectly() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password123")
                .admin(true)
                .build();

        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("jane.doe@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("Jane");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAdmin()).isTrue();
    }

    @Test
    void getAuthorities_shouldReturnEmptySet() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("First")
                .lastName("Last")
                .password("pass")
                .admin(false)
                .build();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities)
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isEmpty();
    }

    @Test
    void equals_shouldReturnTrue_whenIdsAreEqual() {
        UserDetailsImpl u1 = UserDetailsImpl.builder()
                .id(1L)
                .username("user1@example.com")
                .firstName("A")
                .lastName("B")
                .password("pass")
                .admin(false)
                .build();

        UserDetailsImpl u2 = UserDetailsImpl.builder()
                .id(1L)
                .username("user2@example.com")
                .firstName("C")
                .lastName("D")
                .password("pass2")
                .admin(true)
                .build();

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.equals(u2)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenIdsDiffer() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(1L).username("a").password("x").build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(2L).username("b").password("y").build();

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    void equals_shouldReturnFalse_whenOtherObjectIsNullOrDifferentType() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("x").password("y").build();

        assertThat(user.equals(null)).isFalse();
        assertThat(user.equals("not a user")).isFalse();
    }

    @Test
    void allAccountMethods_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("x").password("y").build();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}