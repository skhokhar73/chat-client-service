package in.skippr.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AiMemoryChatRequest extends AiRequest {
    private String conversationId;
}
