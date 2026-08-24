package in.skippr.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class AiResponse implements Serializable {
    private String responseText;

}
