package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentRequest;
import ru.practicum.ewm.dto.NewCommentResponse;
import ru.practicum.ewm.dto.UpdateCommentResponse;

import java.util.List;

public interface CommentService {
    NewCommentResponse postComment(long userId, long eventId, CommentRequest commentRequest);

    UpdateCommentResponse patchComment(long userId, long commentId, CommentRequest commentRequest);

    void deleteCommentByUser(long userId, long commentId);

    List<NewCommentResponse> getAllCommentsOnEvent(long eventId);

    List<NewCommentResponse> getAllCommentsOnEventByUser(long eventId, long userId);

    void deleteCommentByAdmin(long commentId);
}
