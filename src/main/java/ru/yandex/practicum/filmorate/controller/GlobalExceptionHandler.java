package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new TreeMap<>();
        errors.put("Error", "Некорректное значение в теле запроса");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put("Details", error.getField() + " / " + error.getDefaultMessage());
        }
        log.error("Ошибка валидации тела запроса." + errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new TreeMap<>();
        errors.put("Error", "Некорректное значение в переменной пути/параметре запроса");
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errors.put("Details", violation.getPropertyPath().toString() + " / " + violation.getMessage());
        }
        log.error("Ошибка валидации переменной пути/параметра запроса." + errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        log.error("Ошибка валидации Id. " + e.getMessage());
        return new ResponseEntity<>(Map.of("Error", "Ошибка валидации Id объекта" , "Details", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleDataNotFoundException(DataNotFoundException e) {
        log.error("Ошибка валидации Id. " + e.getMessage());
        return new ResponseEntity<>(Map.of("Error", "Id объекта не найден" , "Details", e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
