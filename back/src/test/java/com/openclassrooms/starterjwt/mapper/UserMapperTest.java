package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_shouldMapDtoToEntity() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto(1L, "test@mail.com", "TestLastname", "TestFirstname", false, "password", now, now);

        User entity = mapper.toEntity(dto);

        assertThat(entity.getEmail()).isEqualTo("test@mail.com");
        assertThat(entity.getFirstName()).isEqualTo("TestFirstname");
        assertThat(entity.isAdmin()).isFalse();
    }

    @Test
    void toEntity_shouldMapDtoToNullWhenNull() {

        User user = mapper.toEntity((UserDto) null);
        List<User> users = mapper.toEntity((List<UserDto>) null);

        assertThat(user).isNull();
        assertThat(users).isNull();
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        LocalDateTime now = LocalDateTime.now();
        User entity = new User(1L, "test@mail.com", "TestLastname", "TestFirstname", "pass", true, now, now);

        UserDto dto = mapper.toDto(entity);

        assertThat(dto.getEmail()).isEqualTo("test@mail.com");
        assertThat(dto.getFirstName()).isEqualTo("TestFirstname");
        assertThat(dto.isAdmin()).isTrue();
    }


    @Test
    void toDto_shouldMapEntityToNullWhenNull() {

        UserDto dto = mapper.toDto((User) null);
        List<UserDto> dtos = mapper.toDto((List<User>) null);

        assertThat(dto).isNull();
        assertThat(dtos).isNull();
    }

    @Test
    void listMapping_shouldMapListsCorrectly() {
        User u = new User(1L, "test@mail.com", "TestLastname", "TestFirstname", "pwd", false, null, null);
        List<User> users = Arrays.asList(u);
        List<UserDto> dtos = mapper.toDto(users);
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getEmail()).isEqualTo("test@mail.com");

        List<User> entities = mapper.toEntity(dtos);
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getFirstName()).isEqualTo("TestFirstname");
    }
}