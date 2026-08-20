package in.skippr.controller;

import in.skippr.model.AiMemoryChatRequest;
import in.skippr.model.AiMemoryChatResponse;
import in.skippr.model.AiResponse;
import in.skippr.tool.AddressTools;
import in.skippr.tool.AgeTools;
import in.skippr.tool.DateTimeTools;
import in.skippr.tool.FeelingTool;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.autoconfigure.ToolSearchAdvisorProperties;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityChecker;
import org.springframework.ai.tool.toolsearch.ToolIndex;
import org.springframework.ai.tool.toolsearch.eviction.CompositeEvictionStrategy;
import org.springframework.ai.tool.toolsearch.eviction.LruEvictionStrategy;
import org.springframework.ai.tool.toolsearch.eviction.ToolIndexEvictionStrategy;
import org.springframework.ai.tool.toolsearch.eviction.TtlEvictionStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/chat-client-service/v1/tool-calling/tool-search-advisor")
@Log4j2
public class ToolCallingToolSearchAdvisorAiController {
    private final ChatClient chatClient;

    public ToolCallingToolSearchAdvisorAiController(ChatModel chatModel, ToolSearchAdvisorProperties properties,
                                                    ToolCallingManager toolCallingManager, ToolIndex toolIndex,
                                                    ObjectProvider<ToolExecutionEligibilityChecker> toolExecutionEligibilityChecker) {
        var builder = ToolSearchToolCallingAdvisor.builder()
                .toolCallingManager(toolCallingManager)
                .toolIndex(toolIndex)
                .advisorOrder(properties.getAdvisorOrder())
                .referenceToolNameAccumulation(properties.isReferenceToolNameAccumulation())
                .sessionIdKeyName(properties.getSessionIdKeyName())
                .evictionStrategy(buildEvictionStrategy(properties.getEviction()));


        if (properties.getMaxResults() != null) {
            builder.maxResults(properties.getMaxResults());
        }
        if (StringUtils.hasText(properties.getSystemMessageSuffix())) {
            builder.systemMessageSuffix(properties.getSystemMessageSuffix());
        }

        toolExecutionEligibilityChecker.ifAvailable(builder::toolExecutionEligibilityChecker);


        chatClient = ChatClient.builder(chatModel)
                .defaultTools(new AgeTools(), new AddressTools())
                .defaultAdvisors(builder.build())
                .build();

    }
    @PostMapping("/vector/ai")
    public AiResponse generation(@RequestBody AiMemoryChatRequest request) {
        final String conversationId;
        if (request.getConversationId() == null) {
            conversationId = UUID.randomUUID().toString();
        } else {
            conversationId = request.getConversationId();
        }
        String content = this.chatClient.prompt(request.getUserInput())
                .advisors(new SimpleLoggerAdvisor())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .tools(new DateTimeTools(), new FeelingTool())
                .call()
                .content();
        return new AiMemoryChatResponse(content, conversationId);
    }


    private static ToolIndexEvictionStrategy buildEvictionStrategy(ToolSearchAdvisorProperties.Eviction eviction) {
        LruEvictionStrategy lru = new LruEvictionStrategy(eviction.getLruMaxSessions());
        if (eviction.getTtl() != null) {
            return new CompositeEvictionStrategy(lru, new TtlEvictionStrategy(eviction.getTtl()));
        }
        return lru;
    }

}
