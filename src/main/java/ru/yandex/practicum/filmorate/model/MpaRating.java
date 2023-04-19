package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MpaRating {
    private final Long id;
    @NotBlank
    private final String name;
}
