package in.skippr.controller;

import in.skippr.model.AiRequest;
import in.skippr.model.AiResponse;
import in.skippr.tool.AgentThinking;
import in.skippr.tool.DateTimeTools;
import in.skippr.tool.FeelingTool;
import in.skippr.tool.NameTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.augment.AugmentedToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-client-service/v1/tool-calling")
@Log4j2
public class ToolCallingAiController {
    private final ChatClient chatClient;
    private final ChatClient chatClientAugmentedArgument;

    public ToolCallingAiController(ChatClient.Builder chatClientBuilder) {

        this.chatClient = chatClientBuilder.build();
        AugmentedToolCallbackProvider<AgentThinking> provider = AugmentedToolCallbackProvider
                .<AgentThinking>builder()
                .toolObject(new NameTools())  // Your @Tool annotated class
                .argumentType(AgentThinking.class)
                .argumentConsumer(event -> {
                    AgentThinking thinking = event.arguments();
                    log.info("Tool: {} | Reasoning: {} and Confidence: {}", event.toolDefinition().name(), thinking.innerThought(), thinking.confidence());
                })
                .removeExtraArgumentsAfterProcessing(true)
                .build();

        this.chatClientAugmentedArgument = chatClientBuilder.defaultTools(provider).build();
    }
    @PostMapping("/ai")
    public AiResponse generation(@RequestBody AiRequest userInput) {
        return AiResponse.builder()
                .responseText(this.chatClient.prompt(userInput.getUserInput())
                        .advisors(new SimpleLoggerAdvisor())
                        .tools(new DateTimeTools(), new FeelingTool())
                        .call()
                        .content())
                .build();
    }

    @PostMapping("/agent-thinking/ai")
    public AiResponse generationAgentThinking(@RequestBody AiRequest userInput) {
        return AiResponse.builder()
                .responseText(this.chatClientAugmentedArgument.prompt(userInput.getUserInput())
                        .advisors(new SimpleLoggerAdvisor())
                        .call()
                        .content())
                .build();
    }
}
