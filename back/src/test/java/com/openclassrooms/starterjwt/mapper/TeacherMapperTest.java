package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class TeacherMapperTest {
    private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    void toEntity_shouldMapDtoToEntity() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto = new TeacherDto(1L, "Teacherlastname", "Teacherfirstname", now, now);

        Teacher entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getLastName()).isEqualTo("Teacherlastname");
        assertThat(entity.getFirstName()).isEqualTo("Teacherfirstname");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_shouldMapDtoToNullWhenNull() {

        Teacher teacher = mapper.toEntity((TeacherDto) null);
        List<Teacher> teachers = mapper.toEntity((List<TeacherDto>) null);

        assertThat(teacher).isNull();
        assertThat(teachers).isNull();
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        LocalDateTime now = LocalDateTime.now();
        Teacher entity = new Teacher(1L, "Teacherlastname", "Teacherfirstname", now, now);

        TeacherDto dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLastName()).isEqualTo("Teacherlastname");
        assertThat(dto.getFirstName()).isEqualTo("Teacherfirstname");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDto_shouldMapEntityToNullWhenNull() {
        TeacherDto dto = mapper.toDto((Teacher) null);
        List<TeacherDto> dtos = mapper.toDto((List<Teacher>) null);

        assertThat(dto).isNull();
        assertThat(dtos).isNull();
    }

    @Test
    void listMapping_shouldMapListsCorrectly() {
        Teacher t = new Teacher(1L, "Teacherlastname", "Teacherfirstname", null, null);
        List<Teacher> teachers = Arrays.asList(t);
        List<TeacherDto> dtos = mapper.toDto(teachers);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getLastName()).isEqualTo("Teacherlastname");

        List<Teacher> entities = mapper.toEntity(dtos);
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getFirstName()).isEqualTo("Teacherfirstname");
    }
}