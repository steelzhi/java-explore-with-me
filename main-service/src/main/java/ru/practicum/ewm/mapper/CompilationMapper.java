package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

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
}