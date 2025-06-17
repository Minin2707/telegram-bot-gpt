package hicks88.telegram_bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class BotConfig {
    private String username;
    private String token;
}