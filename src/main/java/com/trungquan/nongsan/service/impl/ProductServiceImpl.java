package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.dto.ProductDto;
import com.trungquan.nongsan.dto.UserSearchDTO;
import com.trungquan.nongsan.constant.SortType;
import com.trungquan.nongsan.dto.ProductSearchDTO;
import com.trungquan.nongsan.dto.MonthlyRevenueDTO;
import com.trungquan.nongsan.entity.*;
import com.trungquan.nongsan.repository.*;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.service.CategoryService;
import com.trungquan.nongsan.spec.ProductSpec;
import com.trungquan.nongsan.service.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    CategoryService categoryService;
    FileUploadService fileUploadService;
    OrderDetailRepository orderDetailRepository;
    PromotionRepository promotionRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAllActive() {
        return productRepository.findAllByActiveFlag(true);
    }

    @Override
    public void addProduct(Product product, MultipartFile coverImage) throws IOException {
        Product savedProduct = productRepository.save(product);
        savedProduct.setCoverImage(fileUploadService.uploadFile(coverImage));
        productRepository.save(savedProduct);
    }

    @Override
    public void editProduct(Product product, MultipartFile coverImage) throws IOException {
        Product savedProduct = productRepository.save(product);
        if (!coverImage.isEmpty()) {
            savedProduct.setCoverImage(fileUploadService.uploadFile(coverImage));
            productRepository.save(savedProduct);
        }
    }

    @Override
    public void deleteProduct(Long id) throws Exception {
        List<OrderDetail> orderDetailsFindByProductId = orderDetailRepository.findByProductId((id));
        if(!orderDetailsFindByProductId.isEmpty()){
            throw new Exception("Nông Sản đã có trong các đơn hàng , vui lòng xoá các đơn hàng có nông sản trước");
        }
        productRepository.deleteById(id);
    }

    @Override
    public void setActiveFlag(Long id, boolean activeFlag) throws Exception {
        Product productById = productRepository.findById(id).orElse(null);
        if(productById == null){
            throw new Exception("Không tìm thấy nông sản với id này");
        }
        productById.setActiveFlag(activeFlag);
        productRepository.save(productById);
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.orElse(null);
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByTitleAndActiveFlag(name, true);
    }
    private String generateUniqueFileName(String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "_" + originalFileName;
    }


    @Override
    public Page<Product> searchProducts(ProductSearchDTO search, Pageable pageable) {
        Long categoryId = search.getCategoryId();
        String keyword = search.getKeyword();

        // Lấy dữ liệu phân trang dựa trên categoryId, keyword và các điều kiện tìm kiếm khác (nếu có)
        if (categoryId != null && keyword != null) {
            return productRepository.findByCategory_IdAndTitleContainingAndActiveFlag(categoryId, keyword, true, pageable);
        } else if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            return productRepository.findByCategoryAndActiveFlag(category, true, pageable);

        } else if (keyword != null) {
            return productRepository.findByTitleContainingAndActiveFlag(keyword, true, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Product> searchProductsUser(UserSearchDTO search, Pageable pageable) {
        String keyword = search.getKeyword();
        Long categoryId = search.getCategoryId();
        String sortBy = search.getSortBy();
        String amountGap = search.getAmountGap();

        // Parse price range (format: "min - max", e.g. "0 - 500000")
        Double priceMin = null;
        Double priceMax = null;
        if (amountGap != null && !amountGap.trim().isEmpty()) {
            try {
                String[] parts = amountGap.split(" - ");
                if (parts.length >= 2) {
                    priceMin = Double.parseDouble(parts[0].trim().replace(".", ""));
                    priceMax = Double.parseDouble(parts[1].trim().replace(".", ""));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // Build dynamic Specification with AND logic
        Specification<Product> spec = Specification.where(ProductSpec.isActive());

        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(ProductSpec.hasKeyword(keyword));
        }
        if (categoryId != null) {
            spec = spec.and(ProductSpec.hasCategory(categoryId));
        }
        if (priceMin != null || priceMax != null) {
            spec = spec.and(ProductSpec.priceBetween(priceMin, priceMax));
        }

        // Apply sorting via pageable
        Pageable sortedPageable = pageable;
        if ("priceLowToHigh".equals(sortBy)) {
            sortedPageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "salePrice"));
        } else if ("priceHighToLow".equals(sortBy)) {
            sortedPageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "salePrice"));
        } else if ("newest".equals(sortBy)) {
            sortedPageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        } else if ("oldest".equals(sortBy)) {
            sortedPageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt"));
        }

        return productRepository.findAll(spec, sortedPageable);
    }

    @Override
    public Page<Product> getAllProductsForUsers(Pageable pageable) {
        return productRepository.findByActiveFlag(true, pageable);
    }

    @Override
    public List<Product> getTop4BestSeller() {
        return productRepository.findTop4ByActiveFlagOrderByBuyCountDesc(true);
    }

    @Override
    public List<Product> getTop10BestSeller() {
        return productRepository.findTop10ByActiveFlagOrderByTotalRevenueDesc(true);
    }

    @Override
    public List<ProductDto> getTop10BestSellerByMonth(int month) {
        List<Object[]> result = productRepository.findTop10BestSellerByMonth(month);
        List<ProductDto> resultConvertedToDto = new ArrayList<>();
        for (Object[] item : result) {
            resultConvertedToDto.add(new ProductDto(item[0].toString(), Double.parseDouble(item[1].toString())));
        }
        return resultConvertedToDto;
    }

    @Override
    public List<Product> findAllOrderByCreatedDate() {
        return productRepository.findByActiveFlagOrderByCreatedAtDesc(true);
    }

    @Override
    public Set<Product> getFavoriteProductsByUserId(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return user.getFavoriteProducts();
        }
        return Collections.emptySet();
    }

    @Override
    public Long countProduct() {
        return productRepository.count();
    }

    @Override
    public List<Product> getAllProductsByCategoryId(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        return productRepository.findAllByCategoryAndActiveFlag(category, true);
    }

    @Override
    public Page<Product> getAllProductsPaginatedByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveFlag(categoryId, true, pageable);
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthRevenuePerYear(int year) {
        List<Object[]> result = productRepository.findMonthlyRevenue(year);
        List<MonthlyRevenueDTO> resultConvertedToDto = new ArrayList<>();
        for (Object[] item : result) {
            resultConvertedToDto.add(new MonthlyRevenueDTO(Integer.parseInt(item[0].toString()), Double.parseDouble(item[1].toString())));
        }
        return resultConvertedToDto;
    }

    @Override
    public void addProductToPromotion(Long productId){
        Promotion promotion = promotionRepository.findClosestActivePromotion();
        if(promotion == null) {
            throw new IllegalStateException("Hiện tại không có chương trình khuyến mãi nào.");
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setPromotion(promotion);
            product.setSalePrice(product.getOriginalPrice() * (100.0 - promotion.getDiscountPercentage()) / 100.0);
            productRepository.save(product);
        }
    }

    @Override
    public void removeProductFromPromotion(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setPromotion(null);
            product.setSalePrice(product.getOriginalPrice());
            productRepository.save(product);
        }
    }
}
