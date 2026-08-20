package in.skippr.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

@Log4j2
public class AgeTools {

    @Tool(description = "Get the my age", name = "getMyAge")
    String getMyAge() {
        log.info("Getting my age: {}", LocaleContextHolder.getLocale());
        return "25";
    }

}