package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MetaDataStorage;

import java.util.List;

@Service
public class MetaDataService {

    private final MetaDataStorage metaDataStorage;

    @Autowired
    public MetaDataService(MetaDataStorage metaDataStorage) {
        this.metaDataStorage = metaDataStorage;
    }

    public List<Genre> getAllGenres() {
        return metaDataStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        return metaDataStorage.getGenreById(id);
    }

    public List<MpaRating> getAllMpaRating() {
        return metaDataStorage.getAllMpaRating();
    }

    public MpaRating getMpaRatingById(Long id) {
        return metaDataStorage.getMpaRatingById(id);
    }

}
