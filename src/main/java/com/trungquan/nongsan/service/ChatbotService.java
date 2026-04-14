package com.trungquan.nongsan.service;

import com.trungquan.nongsan.dto.chat.ChatbotResponse;
import com.trungquan.nongsan.entity.Product;

import java.util.List;

public interface ChatbotService {
    ChatbotResponse processMessage(String message);

    List<Product> searchProductsByKeywords(List<String> keywords);
}
