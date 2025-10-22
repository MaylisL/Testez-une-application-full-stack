package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test@mail.com")
                .firstName("testFirstname")
                .lastName("testLastname")
                .password("hashedpassword")
                .admin(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private UserDto createUserDto(Long id) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail("test@mail.com");
        dto.setFirstName("testFirstname");
        dto.setLastName("testLastname");
        dto.setAdmin(false);
        dto.setPassword("hashedpassword");
        dto.setCreatedAt(LocalDateTime.now().minusDays(1));
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        User user = createUser(1L);
        UserDto dto = createUserDto(1L);

        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$.firstName").value("testFirstname"))
                .andExpect(jsonPath("$.lastName").value("testLastname"));
    }

    @Test
    void findById_shouldReturnNotFound_whenUserNotExists() throws Exception {
        Mockito.when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/user/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturnOk_whenUserDeletesSelf() throws Exception {
        User user = createUser(1L);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        UserDetails mockDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(mockDetails, null, mockDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnUnauthorized_whenUserDeletesOtherUser() throws Exception {
        User user = createUser(1L);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        UserDetails mockDetails = org.springframework.security.core.userdetails.User
                .withUsername("test2@mail.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(mockDetails, null, mockDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_shouldReturnNotFound_whenUserNotExists() throws Exception {
        Mockito.when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/99"))
                .andExpect(status().isNotFound());
    }

}