package in.skippr.controller;

import in.skippr.model.AiMemoryChatRequest;
import in.skippr.model.AiMemoryChatResponse;
import in.skippr.model.AiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/chat-client-service/v1/memory")
@Log4j2
public class MemoryChatClientController {

    private final ChatClient chatClient;

    public MemoryChatClientController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {

        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
    @PostMapping("/ai")
    public AiMemoryChatResponse generation(@RequestBody AiMemoryChatRequest request) {
        final String conversationId;
        if (request.getConversationId() == null) {
            conversationId = UUID.randomUUID().toString();
        } else {
            conversationId = request.getConversationId();
        }

        String content = this.chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(request.getUserInput())
                .call()
                .content();

        return new AiMemoryChatResponse(content, conversationId);
    }
}
