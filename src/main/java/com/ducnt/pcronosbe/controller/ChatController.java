package com.ducnt.pcronosbe.controller;

import com.ducnt.pcronosbe.dto.ChatRequest;
import com.ducnt.pcronosbe.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-url}")
@Tag(name = "Chat API", description = "API for chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    OllamaChatModel ollamaChatModel;

    @PostMapping("/chat")
    @Operation(summary = "chat", description = "chat")
    public ApiResponse chat(@RequestBody ChatRequest request) {
        try {
            var prompt = new Prompt(request.getMessage());
            ChatResponse response = ollamaChatModel.call(prompt);

            return ApiResponse
                    .builder()
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse
                    .builder()
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
