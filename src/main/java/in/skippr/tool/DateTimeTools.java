package in.skippr.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone", name = "getCurrentDateTime")
    String getCurrentDateTime() {
        log.info("Getting current date and time in user's timezone: {} using tool calling: {}", LocaleContextHolder.getTimeZone(), LocaleContextHolder.getLocale());
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Set a user alarm for the given time", name = "setAlarm")
    void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        log.info("Alarm set for {}", alarmTime);
    }
}