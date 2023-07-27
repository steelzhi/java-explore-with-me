package ru.practicum.ewm.util;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.IncorrectCategoryRequestException;
import ru.practicum.ewm.exception.IncorrectEventRequestException;
import ru.practicum.ewm.exception.IncorrectParamsException;
import ru.practicum.ewm.exception.IncorrectUserRequestException;

import java.time.LocalDateTime;

public class ControllerParamChecker {
    private ControllerParamChecker() {
    }

    public static void checkQueryParams(long... params) {
        for (long param : params) {
            if (param == 0) {
                throw new IncorrectParamsException("Введены некорректные параметры запроса");
            }
        }

    }
}
