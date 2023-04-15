package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendForUser {
    private final Long userId;
    private final Long friendId;
}
