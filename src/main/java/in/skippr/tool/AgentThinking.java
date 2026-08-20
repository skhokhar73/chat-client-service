package in.skippr.tool;

import org.springframework.ai.tool.annotation.ToolParam;

public record AgentThinking (
    @ToolParam(description = "Your reasoning for calling this tool")
    String innerThought,

    @ToolParam(description = "Confidence level (low, medium, high)", required = false)
    String confidence
) {}