package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MetaDataStorage {

    List<Genre> getAllGenres();

    Genre getGenreById(Long id);

    List<MpaRating> getAllMpaRating();

    MpaRating getMpaRatingById(Long id);
}
