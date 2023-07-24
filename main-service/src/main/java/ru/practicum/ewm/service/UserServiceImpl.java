package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.UserDto;
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
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        List<User> users = new ArrayList<>();

        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
            Page<User> pagedList;
            if (ids != null) {
                pagedList = userRepository.getAllUsersByIdIn(ids, page);
            } else {
                pagedList = userRepository.getAllUsers(page);
            }
            if (pagedList != null) {
                users = pagedList.getContent();
            }
        }

        List<UserDto> userDtos = UserMapper.mapToUserDto(users);
        return userDtos;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
