package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // given
        User user = User.builder()
                .id(1L)
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));

        // when
        UserDetails result = userDetailsService.loadUserByUsername("jane.doe@example.com");

        // then
        assertThat(result).isInstanceOf(UserDetailsImpl.class);
        UserDetailsImpl userDetails = (UserDetailsImpl) result;
        assertThat(userDetails.getUsername()).isEqualTo("jane.doe@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("Jane");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getId()).isEqualTo(1L);
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        // given
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: unknown@example.com");
    }
}