package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private LoginRequest createLoginRequest() {
        LoginRequest login = new LoginRequest();
        login.setEmail("test@mail.com");
        login.setPassword("password");
        return login;
    }

    private SignupRequest createValidSignupRequest() {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("newtest@mail.com");
        signup.setPassword("password");
        signup.setFirstName("testFirstName");
        signup.setLastName("testLastName");
        return signup;
    }

    private SignupRequest createInvalidSignupRequest() {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("invalid-email");
        signup.setPassword("123");
        signup.setFirstName("A");
        signup.setLastName("");
        return signup;
    }


    @Test
    void authenticateUser_shouldReturnJwtResponse_whenCredentialsValid() throws Exception {

        LoginRequest request = createLoginRequest();

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "test@mail.com",
                "Testname",
                "Testlastname",
                true,
                "password"
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt");

        User mockUser = User.builder()
                .id(1L)
                .email("test@mail.com")
                .firstName("Testname")
                .lastName("Testlastname")
                .password("encoded")
                .admin(true)
                .build();

        Mockito.when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt"))
                .andExpect(jsonPath("$.username").value("test@mail.com"))
                .andExpect(jsonPath("$.firstName").value("Testname"))
                .andExpect(jsonPath("$.lastName").value("Testlastname"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    void authenticateUser_shouldReturnBadRequest_whenLoginRequestInvalid() throws Exception {

        LoginRequest requestMissingEmail = new LoginRequest();
        requestMissingEmail.setPassword("testpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMissingEmail)))
                .andExpect(status().isBadRequest());

        LoginRequest requestMissingPassword = new LoginRequest();
        requestMissingPassword.setEmail("test@mail.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMissingPassword)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticateUser_shouldReturnDefaultAdminValue_whenNoUserReturnedFromRepository() throws Exception {

        LoginRequest request = createLoginRequest();

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "notfound@mail.com",
                "Testname",
                "Testlastname",
                true,
                "password"
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt");

        Mockito.when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt"))
                .andExpect(jsonPath("$.username").value("notfound@mail.com"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void authenticateUser_shouldReturnUnauthorized_whenAuthenticationFails() throws Exception {
        LoginRequest request = createLoginRequest();

        Mockito.when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_shouldReturnOk_whenRegistrationSuccessful() throws Exception {
        SignupRequest signup = createValidSignupRequest();

        Mockito.when(userRepository.existsByEmail("newtest@mail.com")).thenReturn(false);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedpass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {
        SignupRequest signup = createValidSignupRequest();
        signup.setEmail("existing@mail.com");

        Mockito.when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenSignupRequestIsInvalid() throws Exception {
        SignupRequest signup = createInvalidSignupRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest());
    }
}
