package hicks88.telegram_bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigurationTest implements CommandLineRunner {

    private final BotConfig botConfig;
    private final OpenAIConfig openAIConfig;

    public ConfigurationTest(BotConfig botConfig, OpenAIConfig openAIConfig) {
        this.botConfig = botConfig;
        this.openAIConfig = openAIConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Configuration Test ===");
        log.info("Bot Username: {}", botConfig.getUsername());
        log.info("Bot Token: {}", botConfig.getToken() != null ? "SET" : "NOT SET");
        log.info("OpenAI API Key: {}", openAIConfig.getApiKey() != null ? "SET" : "NOT SET");
        log.info("OpenAI Model: {}", openAIConfig.getModel());
        log.info("OpenAI Max Tokens: {}", openAIConfig.getMaxTokens());
        log.info("==========================");
    }
}