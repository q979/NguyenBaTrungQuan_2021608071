package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.dto.chat.ChatbotRequest;
import com.trungquan.nongsan.dto.chat.ChatbotResponse;
import com.trungquan.nongsan.service.ChatbotService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatController {

    ChatbotService chatbotService;

    @GetMapping
    public String getChatPage() {
        return "user/chat";
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<ChatbotResponse> sendMessage(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatbotService.processMessage(request.getMessage());
        return ResponseEntity.ok(response);
    }

}
