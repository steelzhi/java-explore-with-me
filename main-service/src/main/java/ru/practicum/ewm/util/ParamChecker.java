package ru.practicum.ewm.util;

import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.IncorrectCategoryRequestException;
import ru.practicum.ewm.exception.IncorrectUserRequestException;

public class ParamChecker {
    private ParamChecker() {
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
    }
}
