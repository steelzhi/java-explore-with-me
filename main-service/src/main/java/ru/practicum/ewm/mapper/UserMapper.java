package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = null;
        if (user != null) {
            userDto = new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getName()
            );
        }
        return userDto;
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                userDtos.add(mapToUserDto(user));
            }
        }
        return userDtos;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        UserShortDto userShortDto = null;
        if (user != null) {
            userShortDto = new UserShortDto(
                    user.getId(),
                    user.getName()
            );
        }
        return userShortDto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = null;
        if (userDto != null) {
            user = new User(
                    userDto.getId(),
                    userDto.getEmail(),
                    userDto.getName()
            );
        }
        return user;
    }
}