package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserHaveLike extends RuntimeException{
    public UserHaveLike(String message) {
        super(message);
    }
}
