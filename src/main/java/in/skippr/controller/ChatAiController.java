package in.skippr.controller;

import in.skippr.model.AiRequest;
import in.skippr.model.AiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat-client-service/v1")
@Log4j2
public class ChatAiController {

    private final ChatClient chatClient;

    public ChatAiController(ChatClient.Builder chatClientBuilder) {

        this.chatClient = chatClientBuilder.defaultSystem("You are a funny and super sarcastic. Feel free to add some lies and jokes in answer")
                .defaultAdvisors(ToolCallingAdvisor.builder()
                        .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 300)
                        .build())
                .build();
    }

    @PostMapping("/ai")
    public AiResponse generation(@RequestBody AiRequest userInput) {
        return AiResponse.builder()
                .responseText(this.chatClient.prompt()
                        .tools()
                        .advisors(new SimpleLoggerAdvisor())
                        .user(userInput.getUserInput())
                        .call()
                        .content())
                .build();
    }

    @GetMapping("/ai")
    public List<String> generation() {
        return chatClient.prompt()
                .user(u -> u.text("List five {subject}")
                        .param("subject", "ice cream flavors"))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
    }

    @GetMapping("/ai/prompt-template")
    public List<String> promptTemplate() {
        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());

        String format = listOutputConverter.getFormat();
        String template = """
        List five {subject}
        {format}
        """;

        Prompt prompt = PromptTemplate.builder().template(template).variables(Map.of("subject", "ice cream flavors", "format", format)).build().create();

        Generation generation = this.chatClient.prompt(prompt).call().chatResponse().getResult();

        return listOutputConverter.convert(generation.getOutput().getText());
    }
}