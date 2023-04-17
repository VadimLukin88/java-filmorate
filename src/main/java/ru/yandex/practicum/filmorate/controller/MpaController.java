package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    private MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<MpaRating> getAllMpaRating() {
        return mpaService.getAllMpaRating();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaRatingById(@PathVariable
                                      @NotNull(message = "Не указан Id жанра")
                                      Long id)  {
        return mpaService.getMpaRatingById(id);
    }

}
