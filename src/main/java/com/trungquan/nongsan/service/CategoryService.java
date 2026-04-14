package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Category;
import com.trungquan.nongsan.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<Category> getAllCategories(Pageable pageable);

    List<Category> getAllCategories();

    Category getCategoryById(Long categoryId);

    void addCategory(Category category);

    void updateCategory(Long categoryId, Category updatedCategory);

    void deleteCategory(Long categoryId);

    List<CategoryDto> getTop10BestSellerByMonth(int selectedValue);

}
