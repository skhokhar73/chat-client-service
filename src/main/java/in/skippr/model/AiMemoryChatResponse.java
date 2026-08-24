package in.skippr.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiMemoryChatResponse extends AiResponse {
    private String conversationId;

    public AiMemoryChatResponse(String responseText, String conversationId) {
        super(responseText);
        this.conversationId = conversationId;
    }
}
