package hicks88.telegram_bot.bot;

import hicks88.telegram_bot.config.BotConfig;
import hicks88.telegram_bot.service.RecipeService;
import hicks88.telegram_bot.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipeBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final RecipeService recipeService;
    private final ValidationService validationService;

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            log.info("Received message from user {} ({}): {}", userName, chatId, messageText);

            // Обрабатываем команду /start
            if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
                return;
            }

            // Обрабатываем команду /help
            if (messageText.equals("/help")) {
                sendHelpMessage(chatId);
                return;
            }

            // Генерируем рецепты из ингредиентов
            generateAndSendRecipes(chatId, messageText);
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String welcomeMessage = """
            👋 Привет! Я бот-повар, который поможет тебе приготовить вкусные блюда.

            📝 Просто напиши мне список ингредиентов через запятую, и я предложу 3 рецепта блюд, 
            которые можно из них приготовить.

            Пример: картофель, яйца, лук, масло

            Используй /help для получения справки.
            """;

        sendMessage(chatId, welcomeMessage);
    }


    private void sendHelpMessage(long chatId) {
        String helpMessage = """
            🍳 Как пользоваться ботом:

            1. Напиши список ингредиентов через запятую
            2. Я предложу 3 рецепта блюд
            3. Каждый рецепт будет содержать название, ингредиенты и пошаговую инструкцию

            📝 Примеры сообщений:
            • картофель, яйца, лук
            • курица, рис, морковь, лук
            • молоко, мука, яйца, сахар

            ⚠️ Требования:
            • Минимум 2 ингредиента
            • Максимум 20 ингредиентов
            • Только буквы, цифры, пробелы и дефисы

            Команды:
            /start - приветственное сообщение
            /help - эта справка
            """;

        sendMessage(chatId, helpMessage);
    }


    private void generateAndSendRecipes(long chatId, String ingredients) {
        // Валидация входных данных
        ValidationService.ValidationResult validationResult = validationService.validateIngredients(ingredients);

        if (!validationResult.isValid()) {
            sendMessage(chatId, "❌ " + validationResult.getErrorMessage());
            return;
        }

        // Отправляем сообщение о том, что бот обрабатывает запрос
        sendMessage(chatId, "🔍 Ищу рецепты для ингредиентов: " + ingredients + "\n\n⏳ Пожалуйста, подождите...");

        // Генерируем рецепты
        String recipes = recipeService.generateRecipes(ingredients);

        // Отправляем результат
        sendMessage(chatId, "🍽️ Вот что можно приготовить:\n\n" + recipes);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        try {
            execute(message);
            log.info("Message sent to chat {}: {}", chatId, text.substring(0, Math.min(text.length(), 50)) + "...");
        } catch (TelegramApiException e) {
            log.error("Error sending message to chat {}: {}", chatId, e.getMessage());
        }
    }
}