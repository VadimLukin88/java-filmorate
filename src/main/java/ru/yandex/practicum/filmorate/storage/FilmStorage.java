package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilm(Film film); // создание/добавление фильма

    Film updateFilm(Film film); // обновление фильма

    void deleteFilm(Long id);   // удаление фильма

    /** В ТЗ указано:
     * << С помощью аннотации @PathVariable добавьте возможность получать каждый фильм
     * и данные о пользователях по их уникальному идентификатору: GET .../users/{id}. >>
     * Поэтому считаю что нужно добавить в интерфейс метод для извлечения фильма по Id.
     * Аналогично и для интерфейса UserStorage
     */
    Film getFilmById(Long id);  // получение объекта Film по Id


    /** Добавил в интерфейс метод для проверки корректности полученных Id.
     */
    void validateId(Long id);      // проверка корректности Id

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getTopFilms(Integer count);
}
