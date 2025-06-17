package hicks88.telegram_bot.config;

import hicks88.telegram_bot.bot.RecipeBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final RecipeBot recipeBot;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        log.info("Initializing TelegramBotsApi...");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(recipeBot);
            log.info("RecipeBot successfully registered with Telegram API");
        } catch (TelegramApiException e) {
            log.error("Failed to register RecipeBot: {}", e.getMessage(), e);
            throw e;
        }

        return telegramBotsApi;
    }
}