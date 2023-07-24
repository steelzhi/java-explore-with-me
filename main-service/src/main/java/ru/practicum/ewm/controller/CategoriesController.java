package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.util.ParamChecker;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @NotNull NewCategoryDto newCategoryDto) {
        ParamChecker.checkIfCategoryParamsAreNotCorrect(newCategoryDto);
        return categoryService.postCategory(newCategoryDto);
    }


    // после создания событий нужно добавить проверку, что с удаляемой категорией не связано ни одного события!
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@PathVariable Long catId, @RequestBody CategoryDto categoryDto) {
        return categoryService.patchCategory(catId, categoryDto);
    }

}
