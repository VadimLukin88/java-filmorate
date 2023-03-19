# java-filmorate
Template repository for Filmorate project.

### Схема базы данных для приложения

![ER_filmorate.png](https://github.com/VadimLukin88/java-filmorate/blob/main/ER_filmorate.png)

Назначение таблиц
+ Films - хранит основные данные о фильмах (первичный ключ = FilmID)
+ Users - хранит основные данные о пользователях (первичный ключ = UserID)
+ MPA_Rating - возрастные рейтинги фильмов (первичный ключ = MPA_ID)
+ Films_genre - хранит связки FilmID фильма и GenreID
+ Genre - названия жанров (первичный ключ = GenreID)
+ Films_likes - хранит лайки пользователей (FilmID + UserID)
+ Friendship - хранит данные о друзьях пользователей (UserID, FriendID, state - статус заявки в друзья (подтверждена = true / не подтверждена = false)

### Примеры SQL запросов

Возвращаем фильм по ID
~~~~sql
SELECT *
FROM Films
WHERE FilmID = <Id>
~~~~

Возвращаем список друзей пользователя
~~~~sql
SELECT friendID
FROM Friendship
WHERE UserID = <Id> AND state = true
~~~~
Возвращаем список общих друзей
~~~~sql
SELECT friendID
FROM Friendship
WHERE UserID = <User one> AND friendID IN (SELECT friendID
                                           FROM Friendship
                                           WHERE UserID = <User two>)
~~~~

Возвращаем Топ 10 популярных фильмов
~~~~sql
SELECT *  
FROM Films  
WHERE FilmID IN (SELECT FilmID  
                  FROM Films_likes  
                  GROUP BY FilmID  
                  ORDER BY COUNT(FilmID)  DESC
                  LIMIT 10)
~~~~
