package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentRequest;
import ru.practicum.ewm.dto.NewCommentResponse;
import ru.practicum.ewm.dto.UpdateCommentResponse;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private static final int MIN_LENGTH = 20;
    private static final int MAX_LENGTH = 5_000;

    @Override
    public NewCommentResponse postComment(long userId, long eventId, CommentRequest commentRequest) {
        checkIfTextLengthIsNotSuitable(commentRequest);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие с указанным id не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с указанным id не найден"));

        checkIfUserIsInitiator(userId, event);
        checkIfEventStatusDoesNotPermitToPostOrUpdate(event);
        Comment comment = CommentMapper.mapToComment(event, user, commentRequest);
        Comment savedComment = commentRepository.save(comment);

        NewCommentResponse newCommentResponse = CommentMapper.mapToNewCommentResponse(savedComment);
        return newCommentResponse;
    }

    @Override
    @Transactional
    public UpdateCommentResponse patchComment(long userId, long commentId, CommentRequest commentRequest) {
        checkIfTextLengthIsNotSuitable(commentRequest);
        checkIfUserDoesNotExist(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с указанным id не найден"));
        checkIfUserDoesNotHaveRightsToUpdate(userId, comment);
        Event event = comment.getEvent();
        checkIfEventStatusDoesNotPermitToPostOrUpdate(event);

        comment.setText(commentRequest.getText());
        comment.setUpdated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        UpdateCommentResponse updateCommentResponse = CommentMapper.mapToUpdateCommentResponse(savedComment);
        return updateCommentResponse;
    }

    @Override
    @Transactional
    public void deleteCommentByUser(long userId, long commentId) {
        checkIfUserDoesNotExist(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с указанным id не найден"));
        checkIfUserDoesNotHaveRightsToUpdate(userId, comment);

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewCommentResponse> getAllCommentsOnEvent(long eventId) {
        List<Comment> comments = commentRepository.findAllByEvent_Id(eventId);
        List<NewCommentResponse> newCommentResponses = CommentMapper.mapToNewCommentResponse(comments);

        return newCommentResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewCommentResponse> getAllCommentsOnEventByUser(long eventId, long userId) {
        List<Comment> comments = commentRepository.findAllByEvent_IdAndUser_Id(eventId, userId);
        List<NewCommentResponse> newCommentResponses = CommentMapper.mapToNewCommentResponse(comments);

        return newCommentResponses;
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с указанным id не найден"));

        commentRepository.deleteById(commentId);
    }

    private void checkIfTextLengthIsNotSuitable(CommentRequest commentRequest) {
        if (commentRequest.getText() == null
                || commentRequest.getText().isBlank()
                || commentRequest.getText().length() < MIN_LENGTH
                || commentRequest.getText().length() > MAX_LENGTH) {
            throw new IncorrectTextLengthException("Длина текста < 20 либо > 5000 символов");
        }
    }

    private void checkIfUserDoesNotExist(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с указанным id не найден");
        }
    }

    private void checkIfUserDoesNotHaveRightsToUpdate(long userId, Comment comment) {
        if (userId != comment.getUser().getId()) {
            throw new NoRightsToUpdateException(
                    "Комментарий оставлен не данным пользователем - он не имеет прав на редактирование");
        }
    }

    private void checkIfUserIsInitiator(long userId, Event event) {
        if (userId == event.getInitiator().getId()) {
            throw new NoRightsToPostException(
                    "Пользователь не может оставлять комментарии к организуемому им же событию");
        }
    }

    private void checkIfEventStatusDoesNotPermitToPostOrUpdate(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotPermittedStateToPostAndUpdateException(
                    "Добавлять и редактировать комментарии можно только к опубликованным событиям");
        }
    }
}
