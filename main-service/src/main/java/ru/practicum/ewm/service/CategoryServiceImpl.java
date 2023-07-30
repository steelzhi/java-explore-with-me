package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.DuplicateCategoryNameException;
import ru.practicum.ewm.exception.IncorrectCategoryRequestException;
import ru.practicum.ewm.exception.RemovingCategoryException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        checkIfCategoryParamsAreNotCorrect(newCategoryDto);
        checkCategoryName(newCategoryDto.getName());

        Category category = CategoryMapper.mapToCategory(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (eventRepository.countAllByCategory_Id(catId) > 0) {
            throw new RemovingCategoryException("Нельзя удалить данную категорию, т.к. к ней уже привязаны события");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        checkIfCategoryParamsAreNotCorrect(categoryDto);

        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Нет категории с указанным id");
        }

        if (category.get().equals(CategoryMapper.mapToCategory(catId, categoryDto))) {
            return categoryDto;
        }

        checkCategoryName(categoryDto.getName());

        Category patchedCategory = new Category(catId, categoryDto.getName());
        Category savedCategory = categoryRepository.save(patchedCategory);
        return CategoryMapper.mapToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = new ArrayList<>();

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        Page<Category> pagedList = categoryRepository.getAllCategories(page);

        if (pagedList != null) {
            categories = pagedList.getContent();
        }

        return CategoryMapper.mapToCategoryDto(categories);
    }

    @Override
    @Transactional
    public CategoryDto getCategory(Long catId) {
        Optional<Category> foundCategory = categoryRepository.findById(catId);
        if (foundCategory.isEmpty()) {
            throw new CategoryNotFoundException("Нет категории с указанным id");
        }

        return CategoryMapper.mapToCategoryDto(foundCategory.get());
    }

    private void checkCategoryName(String categoryName) {
        if (categoryRepository.findCategoryByName(categoryName) != null) {
            throw new DuplicateCategoryNameException("Категория с таким именем уже существует");
        }
    }

    private void checkIfCategoryParamsAreNotCorrect(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории без имени или с пустым именем");
        }

        if (newCategoryDto.getName().length() > 50) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории с длиной имени > 50 символов");
        }
    }

    private void checkIfCategoryParamsAreNotCorrect(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории без имени или с пустым именем");
        }

        if (categoryDto.getName().length() > 50) {
            throw new IncorrectCategoryRequestException("Попытка добавления категории с длиной имени > 50 символов");
        }
    }
}
