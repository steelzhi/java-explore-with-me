package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.IncorrectUserRequestException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto postUser(UserDto userDto) {
        checkIfUserParamsAreNotCorrect(userDto);
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        List<User> users = new ArrayList<>();

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        Page<User> pagedList;
        if (ids != null) {
            pagedList = userRepository.getAllUsersByIdIn(ids, page);
        } else {
            pagedList = userRepository.getAllUsers(page);
        }
        if (pagedList != null) {
            users = pagedList.getContent();
        }

        List<UserDto> userDtos = UserMapper.mapToUserDto(users);
        return userDtos;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void checkIfUserParamsAreNotCorrect(UserDto userDto) {
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
}
