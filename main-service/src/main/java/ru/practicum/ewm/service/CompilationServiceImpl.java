package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.IncorrectCompilationRequestException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        checkIfCompilationParamsAreNotCorrect(newCompilationDto);
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = EventMapper.mapToEventShortDto(events);
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(savedCompilation, eventShortDtos);
        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isEmpty()) {
            throw new CompilationNotFoundException("Подборка не найдена");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(long id, UpdateCompilationRequest updateCompilationRequest) {
        checkIfCompilationParamsAreNotCorrect(updateCompilationRequest);
        List<Event> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
           // events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            events = eventRepository.getAllEventsByIdInAndState(updateCompilationRequest.getEvents(), EventState.PUBLISHED);
        }
        Compilation currentCompilation = compilationRepository.getReferenceById(id);
        Compilation patchedCompilation = patchCompilation(id, currentCompilation, updateCompilationRequest, events);

        Compilation savedCompilation = compilationRepository.save(patchedCompilation);
        List<EventShortDto> eventShortDtos = EventMapper.mapToEventShortDto(events);
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(savedCompilation, eventShortDtos);
        return compilationDto;
    }

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        Page<Compilation> pagedList = compilationRepository.getCompilations(pinned, page);

        if (pagedList != null) {
            compilations = pagedList.getContent();
        }

        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<EventShortDto> eventShortDtos = EventMapper.mapToEventShortDto(compilation.getEvents());
            CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(compilation, eventShortDtos);
            compilationDtos.add(compilationDto);
        }

        return compilationDtos;
    }

    @Override
    @Transactional
    public CompilationDto getCompilation(long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isEmpty()) {
            throw new CompilationNotFoundException("Подборка не найдена");
        }

        List<EventShortDto> eventShortDtos = EventMapper.mapToEventShortDto(compilation.get().getEvents());
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(compilation.get(), eventShortDtos);

        return compilationDto;
    }

    private void checkIfCompilationParamsAreNotCorrect(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new IncorrectCompilationRequestException("У подборки должен быть заголовок");
        }

        if (newCompilationDto.getTitle().length() < 1 || newCompilationDto.getTitle().length() > 50) {
            throw new IncorrectCompilationRequestException("Некорректная длина заголовка");
        }
    }

    private void checkIfCompilationParamsAreNotCorrect(UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()
                && (updateCompilationRequest.getTitle().length() < 1 || updateCompilationRequest.getTitle().length() > 50)) {
            throw new IncorrectCompilationRequestException("Некорректная длина заголовка");
        }
    }

    private Compilation patchCompilation(long id, Compilation currentcompilation, UpdateCompilationRequest updateCompilationRequest, List<Event> events) {
        Compilation compilation = currentcompilation;

        if (events != null) {
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        return compilation;
    }
}
