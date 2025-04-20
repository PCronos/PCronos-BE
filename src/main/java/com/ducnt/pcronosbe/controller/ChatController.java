package com.ducnt.pcronosbe.controller;

import com.ducnt.pcronosbe.dto.ChatRequest;
import com.ducnt.pcronosbe.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-url}")
@Tag(name = "Chat API", description = "API for chat")
public class ChatController {
    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder clientBuilder) {
        this.chatClient = clientBuilder.build();
    }

    @PostMapping("/chat")
    @Operation(summary = "chat", description = "chat")
    public ApiResponse chat(@RequestBody ChatRequest request) {
        try {
            String response = chatClient
                    .prompt("Tìm kiếm bằng tiếng việt món đồ sau: " + request.getMessage())
                    .call()
                    .content();

            return ApiResponse
                    .builder()
                    .message(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse
                    .builder()
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
