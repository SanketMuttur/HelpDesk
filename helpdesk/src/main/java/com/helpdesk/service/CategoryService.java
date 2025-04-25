package com.helpdesk.service;

import com.helpdesk.dto.CategoryDto;
import com.helpdesk.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategory(Long id);
    Category findOrCreateCategory(String name);
}