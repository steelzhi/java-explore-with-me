package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;

import java.util.List;

public interface CommentService {
    NewCommentResponse postComment(long eventId, long userId, CommentRequest commentRequest);

    UpdateCommentResponse patchComment(long userId, long commentId, CommentRequest commentRequest);

    void deleteComment(long userId, long commentId);

    List<NewCommentResponse> getAllCommentsOnEvent(long eventId);

    List<NewCommentResponse> getAllCommentsOnEventByUser(long eventId, long userId);

}
