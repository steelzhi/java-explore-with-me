package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CommentRequest;
import ru.practicum.ewm.dto.NewCommentResponse;
import ru.practicum.ewm.dto.UpdateCommentResponse;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private CommentMapper() {
    }

    public static Comment mapToComment(Event event, User user, CommentRequest commentRequest) {
        Comment comment = null;
        if (commentRequest != null) {
            comment = new Comment(
                    0,
                    event,
                    user,
                    LocalDateTime.now(),
                    null,
                    commentRequest.getText());
        }
        return comment;
    }

    public static NewCommentResponse mapToNewCommentResponse(Comment comment) {
        NewCommentResponse newCommentResponse = null;
        if (comment != null) {
            newCommentResponse = new NewCommentResponse(
                    comment.getId(),
                    comment.getEvent().getId(),
                    comment.getUser().getId(),
                    comment.getText(),
                    comment.getCreated());
        }
        return newCommentResponse;
    }

    public static UpdateCommentResponse mapToUpdateCommentResponse(Comment comment) {
        UpdateCommentResponse updateCommentResponse = null;
        if (comment != null) {
            updateCommentResponse = new UpdateCommentResponse(
                    comment.getId(),
                    comment.getEvent().getId(),
                    comment.getUser().getId(),
                    comment.getText(),
                    comment.getUpdated()
            );
        }
        return updateCommentResponse;
    }

    public static List<NewCommentResponse> mapToNewCommentResponse(List<Comment> comments) {
        List<NewCommentResponse> newCommentResponses = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                newCommentResponses.add(mapToNewCommentResponse(comment));
            }
        }
        return newCommentResponses;
    }




}