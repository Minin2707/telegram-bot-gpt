package hicks88.telegram_bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAIConfig {
    private String apiKey;
    private String model;
    private Integer maxTokens;
}