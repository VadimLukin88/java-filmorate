# java-filmorate
Template repository for Filmorate project.

### Схема базы данных для приложения

![ER_filmorate_2.png](https://github.com/VadimLukin88/java-filmorate/blob/main/ER_filmorate_2.png)

Назначение таблиц
+ Films - хранит основные данные о фильмах (первичный ключ = FilmID)
+ Users - хранит основные данные о пользователях (первичный ключ = UserID)
+ MPA_Rating - возрастные рейтинги фильмов (первичный ключ = MPA_ID)
+ Films_genre - хранит связки FilmID фильма и GenreID (первичный ключ составной film_id + genre_id)
+ Genre - названия жанров (первичный ключ = GenreID)
+ Films_likes - хранит лайки пользователей (первичный ключ составной film_id + user_id)
+ Friendship - хранит данные о друзьях пользователей (первичный ключ составной user_id + friend_id)

### Примеры SQL запросов

Возвращаем фильм по ID. Жанры к возвращаемому списку фильмов возвращаются отдельным запросом.
~~~~sql
SELECT  f.ID f_id, 
        f.NAME f_name, 
        f.DESCRIPTION f_description, 
        f.RELEASEDATE f_date, 
        f.DURATION f_duration, 
        f.RATE f_rate, 
        mr.ID mr_id, 
        mr.NAME mr_name
FROM  FILMS f 
JOIN MPA_RATING mr ON f.MPA_ID = mr.ID 
WHERE f.ID = 1;
~~~~

Возвращаем список друзей пользователя
~~~~sql
SELECT * FROM users us WHERE us.id IN
        ( SELECT friend_id FROM friendship fs WHERE fs.user_id = 1 );
~~~~
Возвращаем список общих друзей
~~~~sql
SELECT * 
FROM users us 
WHERE us.id IN 
                ( 
                SELECT f1.friend_id FROM friendship f1 WHERE f1.user_id = 1
                INTERSECT
                SELECT f2.friend_id FROM friendship f2 WHERE f2.user_id = 2 
                );
~~~~

Возвращаем Топ 10 популярных фильмов. Жанры к возвращаемому списку фильмов возвращаются отдельным запросом.
~~~~sql
SELECT  f.ID f_id, 
        f.NAME f_name, 
        f.DESCRIPTION f_description, 
        f.RELEASEDATE f_date, 
        f.DURATION f_duration, 
        f.RATE f_rate, 
        mr.ID mr_id, 
        mr.NAME mr_name
FROM  FILMS f 
JOIN MPA_RATING mr ON f.MPA_ID = mr.ID 
ORDER BY f_rate DESC 
LIMIT 10;
~~~~

