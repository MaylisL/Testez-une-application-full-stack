package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    private User createUser(Long id, String email, String firstName, String lastName, boolean admin) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("password123")
                .admin(admin)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return userRepository.save(user);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = createUser(null, "test@mail.com", "Userfirstname", "Userlastname", false);

        User found = userService.findById(user.getId());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void findById_shouldReturnNull_whenUserDoesNotExist() {
        User found = userService.findById(999L);

        assertThat(found).isNull();
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = createUser(null, "testDelete@mail.com", "Userfirstname", "Userlastname", false);

        userService.delete(user.getId());

        User found = userRepository.findById(user.getId()).orElse(null);
        assertThat(found).isNull();
    }

}