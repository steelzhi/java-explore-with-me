package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentRequest;
import ru.practicum.ewm.dto.NewCommentResponse;
import ru.practicum.ewm.dto.UpdateCommentResponse;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @NotNull UserDto userDto) {
        return userService.postUser(userDto);
    }

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/comments/{userId}/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public NewCommentResponse postComment(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @RequestBody CommentRequest commentRequest) {
        return commentService.postComment(userId, eventId, commentRequest);
    }

    @GetMapping("/comments/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<NewCommentResponse> getAllCommentsOnEvent(@PathVariable long eventId) {
        return commentService.getAllCommentsOnEvent(eventId);
    }

    @GetMapping("/comments/{eventId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<NewCommentResponse> getAllCommentsOnEventByUser(@PathVariable long eventId, @PathVariable long userId) {
        return commentService.getAllCommentsOnEventByUser(eventId, userId);
    }

    @PatchMapping("/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateCommentResponse patchComment(@PathVariable long userId,
                                              @PathVariable long commentId,
                                              @RequestBody CommentRequest commentRequest) {
        return commentService.patchComment(userId, commentId, commentRequest);
    }

    @DeleteMapping("/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId, @PathVariable long commentId) {
        commentService.deleteComment(userId, commentId);
    }


}
