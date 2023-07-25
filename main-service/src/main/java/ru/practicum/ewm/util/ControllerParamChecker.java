package ru.practicum.ewm.util;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.IncorrectCategoryRequestException;
import ru.practicum.ewm.exception.IncorrectEventRequestException;
import ru.practicum.ewm.exception.IncorrectUserRequestException;

import java.time.LocalDateTime;

public class ControllerParamChecker {
    private ControllerParamChecker() {
    }

    public static void checkIfUserParamsAreNotCorrect(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя без email или с пустым email");
        }

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя без имени или с пустым именем");
        }

        if (userDto.getName().length() < 2 || userDto.getName().length() > 250) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя со слишком коротким или слишком длинным именем");
        }

        if (userDto.getEmail().length() < 6 || userDto.getEmail().length() > 254) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя со слишком коротким или слишком длинным email");
        }

        if (userDto.getEmail().split("@")[0].length() > 64) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя email длиной > 64 символов перед значком @ ");
        }

        if (userDto.getEmail().split("@")[1].split("\\.")[0].length() > 63) {
            throw new IncorrectUserRequestException("Попытка добавления пользователя email длиной > 63 символов после значка @ ");
        }
    }

    public static void checkIfCategoryParamsAreNotCorrect(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории без имени или с пустым именем");
        }

        if (newCategoryDto.getName().length() > 50) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории с длиной имени > 50 символов");
        }
    }

    public static void checkIfCategoryParamsAreNotCorrect(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории без имени или с пустым именем");
        }

        if (categoryDto.getName().length() > 50) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории с длиной имени > 50 символов");
        }
    }

    public static void checkIfEventParamsAreNotCorrect(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new IncorrectEventRequestException("Дата начала события не может быть раньше, чем через 2 часа от настоящего момента");
        }

        if (newEventDto.getAnnotation() == null || newEventDto.getAnnotation().isBlank()) {
            throw new IncorrectEventRequestException("Краткое описание не должно быть пустым");
        }

        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new IncorrectEventRequestException("Попытка добавления краткого описания со слишком маленьким или слишком большим количество символов");
        }

        if (newEventDto.getDescription() == null || newEventDto.getDescription().isBlank()) {
            throw new IncorrectEventRequestException("Полное описание не должно быть пустым");
        }

        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new IncorrectEventRequestException("Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (newEventDto.getTitle() == null || newEventDto.getTitle().isBlank()) {
            throw new IncorrectEventRequestException("Заголовок не должен быть пустым");
        }

        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new IncorrectEventRequestException("Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }
    }

    public static void checkIfEventParamsAreNotCorrect(UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getAnnotation() != null
                && (updateEventUserRequest.getAnnotation().length() < 20 || updateEventUserRequest.getAnnotation().length() > 2000)) {
            throw new IncorrectEventRequestException("Попытка добавления краткого описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventUserRequest.getDescription() != null
                && (updateEventUserRequest.getDescription().length() < 20 || updateEventUserRequest.getDescription().length() > 7000)) {
            throw new IncorrectEventRequestException("Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventUserRequest.getTitle() != null
                && (updateEventUserRequest.getTitle().length() < 3 || updateEventUserRequest.getTitle().length() > 120)) {
            throw new IncorrectEventRequestException("Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }
    }

    public static void checkIfEventParamsAreNotCorrect(UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getAnnotation() != null
                && (updateEventAdminRequest.getAnnotation().length() < 20 || updateEventAdminRequest.getAnnotation().length() > 2000)) {
            throw new IncorrectEventRequestException("Попытка добавления краткого описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventAdminRequest.getDescription() != null
                && (updateEventAdminRequest.getDescription().length() < 20 || updateEventAdminRequest.getDescription().length() > 7000)) {
            throw new IncorrectEventRequestException("Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventAdminRequest.getTitle() != null
                && (updateEventAdminRequest.getTitle().length() < 3 || updateEventAdminRequest.getTitle().length() > 120)) {
            throw new IncorrectEventRequestException("Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }
    }

}
