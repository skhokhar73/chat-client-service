package in.skippr.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AiRequest implements Serializable {
    private String userInput;

}
