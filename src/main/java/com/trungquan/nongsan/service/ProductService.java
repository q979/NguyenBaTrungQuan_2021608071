package com.trungquan.nongsan.service;

import com.trungquan.nongsan.dto.ProductDto;
import com.trungquan.nongsan.dto.UserSearchDTO;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.dto.ProductSearchDTO;
import com.trungquan.nongsan.dto.MonthlyRevenueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ProductService {

    List<Product> findAll();

    List<Product> findAllActive();

    void addProduct(Product product, MultipartFile coverImage) throws IOException;

    void editProduct(Product product, MultipartFile coverImage) throws IOException;

    void deleteProduct(Long id) throws Exception;

    void setActiveFlag(Long id, boolean activeFlag) throws Exception;

    Product getProductById(Long id);

    Product getProductByName(String name);

    Page<Product> searchProducts(ProductSearchDTO search, Pageable pageable);

    Page<Product> searchProductsUser(UserSearchDTO search, Pageable pageable);

    Page<Product> getAllProductsForUsers(Pageable pageable);

    List<Product> getTop4BestSeller();

    List<Product> getTop10BestSeller();

    List<ProductDto> getTop10BestSellerByMonth(int month);

    List<MonthlyRevenueDTO> getMonthRevenuePerYear(int year);

    List<Product> findAllOrderByCreatedDate();

    Set<Product> getFavoriteProductsByUserId(Long id);

    Long countProduct();

    List<Product> getAllProductsByCategoryId(Long id);

    Page<Product> getAllProductsPaginatedByCategoryId(Long categoryId, Pageable pageable);

    void addProductToPromotion(Long productId);

    void removeProductFromPromotion(Long productId);
}
