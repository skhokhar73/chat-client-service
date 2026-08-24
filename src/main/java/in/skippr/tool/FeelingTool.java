package in.skippr.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

@Log4j2
public class FeelingTool {

    @Tool(description = "Know how I am feeling", name = "getMyFeeling")
    String getMyFeeling() {
        log.info("Getting my feeling: {}", LocaleContextHolder.getLocale());
        return "I am feeling great!";
    }

}