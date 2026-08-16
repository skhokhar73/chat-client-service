package in.skippr.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

@Log4j2
public class NameTools {

    @Tool(description = "Get the my name", name = "getMyName")
    String getMyName() {
        log.info("Getting my name: {}", LocaleContextHolder.getLocale());
        return "John Doe";
    }

}