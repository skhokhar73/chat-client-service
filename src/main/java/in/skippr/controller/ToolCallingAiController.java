package in.skippr.controller;

import in.skippr.model.AiRequest;
import in.skippr.model.AiResponse;
import in.skippr.tool.DateTimeTools;
import in.skippr.tool.FeelingTool;
import in.skippr.tool.NameTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-client-service/v1/tool-calling")
@Log4j2
public class ToolCallingAiController {
    private final ChatClient chatClient;

    public ToolCallingAiController(ChatClient.Builder chatClientBuilder) {

        this.chatClient = chatClientBuilder.build();
    }
    @PostMapping("/ai")
    public AiResponse generation(@RequestBody AiRequest userInput) {
        return AiResponse.builder()
                .responseText(this.chatClient.prompt(userInput.getUserInput())
                        .advisors(new SimpleLoggerAdvisor())
                        .tools(new DateTimeTools(), new NameTools(), new FeelingTool())
                        .call()
                        .content())
                .build();
    }
}
