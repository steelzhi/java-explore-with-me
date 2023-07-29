package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    private CompilationMapper() {
    }

    public static Compilation mapToCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation compilation = null;
        if (newCompilationDto != null) {
            compilation = new Compilation(
                    0,
                    events,
                    newCompilationDto.isPinned(),
                    newCompilationDto.getTitle());
        }
        return compilation;
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos) {
        CompilationDto compilationDto = null;
        if (compilation != null) {
            compilationDto = new CompilationDto(
                    compilation.getId(),
                    eventShortDtos,
                    compilation.isPinned(),
                    compilation.getTitle());
        }
        return compilationDto;
    }

    public static Compilation patchCompilation(long id, UpdateCompilationRequest updateCompilationRequest, List<Event> events) {
        Compilation compilation = null;
        if (updateCompilationRequest != null) {
            compilation = new Compilation(
                    id,
                    events,
                    updateCompilationRequest.isPinned(),
                    updateCompilationRequest.getTitle());
        }
        return compilation;
    }


}