package com.trungquan.nongsan.service;

import com.trungquan.nongsan.dto.ProductDto;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.dto.CategoryDto;
import com.trungquan.nongsan.dto.OrderDTO;

import java.util.List;

public interface ExportService {

    String exportOrderReport(User user, List<OrderDTO> orderDTOList, String keyword);

    String exportCategoryReport(User user, List<CategoryDto> categoryDTOList, String keyword);

    String exportProductReport(User user, List<ProductDto> productDtoList, String keyword);

}
