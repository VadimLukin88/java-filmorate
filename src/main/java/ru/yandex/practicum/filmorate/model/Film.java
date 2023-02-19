package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor     //For JUnit Tests
public class Film {
    private Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Long duration;
}
