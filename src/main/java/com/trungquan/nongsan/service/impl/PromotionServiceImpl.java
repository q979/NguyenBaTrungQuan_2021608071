package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.entity.Promotion;
import com.trungquan.nongsan.repository.ProductRepository;
import com.trungquan.nongsan.repository.PromotionRepository;
import com.trungquan.nongsan.service.FileUploadService;
import com.trungquan.nongsan.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PromotionServiceImpl implements PromotionService {

    PromotionRepository promotionRepository;

    FileUploadService fileUploadService;

    ProductRepository productRepository;

    @Override
    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    @Override
    public List<Promotion> getActivePromotionList() {
        return promotionRepository.findAllByActiveFlagOrderByIdDesc(true);
    }

    @Override
    public Promotion getPromotionById(Long categoryId) {
        return promotionRepository.findById(categoryId)
                .orElse(null);
    }

    @Override
    public Promotion getPromotionByName(String promotionName) {
        return promotionRepository.findByTitle(promotionName)
                .orElse(null);
    }

    @Override
    public void addPromotion(Promotion promotion, MultipartFile image) throws IOException {
        // Implementation for adding a promotion with an image
        Promotion savedPromotion = promotionRepository.save(promotion);
        savedPromotion.setImage(fileUploadService.uploadFile(image));
        promotionRepository.save(savedPromotion);
    }

    @Override
    public void updatePromotion(Promotion updatedPromotion, MultipartFile image) throws IOException {

        Promotion foundedPromotion = promotionRepository.save(updatedPromotion);

        if (!image.isEmpty()) {
            foundedPromotion.setImage(fileUploadService.uploadFile(image));
            promotionRepository.save(foundedPromotion);
        }
    }

    @Override
    public void deletePromotion(Long promotionId) {

        List<Product> productByPromotionId = productRepository.findByPromotionId(promotionId);
        if(!productByPromotionId.isEmpty()){
            productByPromotionId.forEach(product -> {
                product.setPromotion(null);
                productRepository.save(product);
            });
        }
        promotionRepository.deleteById(promotionId);

    }

    @Override
    public void setActiveFlag(Long promotionId, boolean activeFlag) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + promotionId));
        promotion.setActiveFlag(activeFlag);
        promotionRepository.save(promotion);
    }
}
