package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MetaDataService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
public class MetaDataController {
    private final MetaDataService metaDataService;

    @Autowired
    private MetaDataController (MetaDataService metaDataService) {
        this.metaDataService = metaDataService;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return metaDataService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable
                              @NotNull(message = "Не указан Id жанра")
                              Long id)  {
        return metaDataService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MpaRating> getAllMpaRating() {
        return metaDataService.getAllMpaRating();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaRatingById(@PathVariable
                                      @NotNull(message = "Не указан Id жанра")
                                      Long id)  {
        return metaDataService.getMpaRatingById(id);
    }

}
