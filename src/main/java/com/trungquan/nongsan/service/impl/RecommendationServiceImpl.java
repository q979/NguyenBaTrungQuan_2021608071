package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.repository.ProductRepository;
import com.trungquan.nongsan.repository.UserRepository;
import com.trungquan.nongsan.service.RecommendationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RecommendationServiceImpl implements RecommendationService {

    ProductRepository productRepository;
    UserRepository userRepository;
    @Override
    public List<Product> getRecommendationsForUser(Long userId, int limit) {
        // Get the user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        // Get products the user has interacted with (favorites, purchases, etc.)
        Set<Product> userProducts = new HashSet<>();

        // Add favorite products
        if (user.getFavoriteProducts() != null) {
            userProducts.addAll(user.getFavoriteProducts());
        }

        // If user hasn't interacted with any products, return popular products
        if (userProducts.isEmpty()) {
            return productRepository.findTop4ByActiveFlagOrderByBuyCountDesc(true);
        }

        // Extract features from user's products to build a user profile
        Map<String, Integer> categoryFrequency = new HashMap<>();
        Map<String, Integer> producerFrequency = new HashMap<>();
        Map<String, Integer> publisherFrequency = new HashMap<>();

        for (Product product : userProducts) {
            // Count category occurrences
            String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Unknown";
            categoryFrequency.put(categoryName, categoryFrequency.getOrDefault(categoryName, 0) + 1);

            // Count producer occurrences
            String producer = product.getProducer() != null ? product.getProducer() : "Unknown";
            producerFrequency.put(producer, producerFrequency.getOrDefault(producer, 0) + 1);

            // Count publisher occurrences
            String publisher = product.getPublisher() != null ? product.getPublisher() : "Unknown";
            publisherFrequency.put(publisher, publisherFrequency.getOrDefault(publisher, 0) + 1);
        }

        // Get all active products
        List<Product> allProducts = productRepository.findAllByActiveFlag(true);

        // Filter out products the user already has
        List<Product> candidateProducts = allProducts.stream()
                .filter(product -> !userProducts.contains(product))
                .collect(Collectors.toList());

        // Calculate similarity scores for each candidate product
        Map<Product, Double> productScores = new HashMap<>();

        for (Product candidateProduct : candidateProducts) {
            double score = 0.0;

            String productCategory = candidateProduct.getCategory() != null ? candidateProduct.getCategory().getName() : "Unknown";
            if (categoryFrequency.containsKey(productCategory)) {
                score += 3.0 * categoryFrequency.get(productCategory);
            }

            String productAuthor = candidateProduct.getProducer() != null ? candidateProduct.getProducer() : "Unknown";
            if (producerFrequency.containsKey(productAuthor)) {
                score += 2.0 * producerFrequency.get(productAuthor);
            }

            // Publisher similarity (lowest producer)
            String productPublisher = candidateProduct.getPublisher() != null ? candidateProduct.getPublisher() : "Unknown";
            if (publisherFrequency.containsKey(productPublisher)) {
                score += 1.0 * publisherFrequency.get(productPublisher);
            }

            productScores.put(candidateProduct, score);
        }

        return productScores.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getSimilarProducts(Long productId, int limit) {
        Product referenceProduct = productRepository.findById(productId).orElse(null);
        if (referenceProduct == null) {
            return Collections.emptyList();
        }

        List<Product> allProducts = productRepository.findAllByActiveFlag(true).stream()
                .filter(product -> !product.getId().equals(productId))
                .collect(Collectors.toList());

        Map<Product, Double> similarityScores = new HashMap<>();

        for (Product product : allProducts) {
            double score = 0.0;

            if (referenceProduct.getCategory() != null && product.getCategory() != null &&
                    referenceProduct.getCategory().getId().equals(product.getCategory().getId())) {
                score += 3.0;
            }

            if (referenceProduct.getProducer() != null && product.getProducer() != null &&
                    referenceProduct.getProducer().equals(product.getProducer())) {
                score += 2.0;
            }

            if (referenceProduct.getPublisher() != null && product.getPublisher() != null &&
                    referenceProduct.getPublisher().equals(product.getPublisher())) {
                score += 1.0;
            }

            if (referenceProduct.getSalePrice() != null && product.getSalePrice() != null) {
                double priceDiff = Math.abs(referenceProduct.getSalePrice() - product.getSalePrice());
                double maxPrice = Math.max(referenceProduct.getSalePrice(), product.getSalePrice());
                if (priceDiff / maxPrice < 0.2) { // If price difference is less than 20%
                    score += 0.5;
                }
            }

            similarityScores.put(product, score);
        }

        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
