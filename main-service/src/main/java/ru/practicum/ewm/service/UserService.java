package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto postUser(UserDto userDto);

    List<UserDto> getUsers(Long[] ids, Integer from, Integer size);

    void deleteUser(Long id);
}
