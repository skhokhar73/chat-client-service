package in.skippr.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

@Log4j2
public class AddressTools {

    @Tool(description = "Get the my address", name = "getMyAddress")
    String getMyAddress() {
        log.info("Getting my address: {}", LocaleContextHolder.getLocale());
        return "123 Street, City, Country";
    }
}