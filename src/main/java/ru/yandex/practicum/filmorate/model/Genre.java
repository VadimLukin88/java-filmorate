package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Genre implements Comparable<Genre> {
    private final Long id;
    @NotBlank
    private final String name;

    @Override
    public int compareTo(Genre o) {
        if (this.getId() > o.getId()) {
            return 1;
        } else if (this.getId() < o.getId()) {
            return -1;
        } else {
            return 0;
        }
    }

}
