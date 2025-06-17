package hicks88.telegram_bot.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import hicks88.telegram_bot.config.OpenAIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final OpenAIConfig openAIConfig;

    public String generateRecipes(String ingredients) {
        try {
            OpenAiService service = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(30));

            String prompt = String.format(
                    "Из следующих ингредиентов: %s, предложи 3 разных рецепта блюд. " +
                            "Для каждого рецепта укажи: название блюда, список ингредиентов, пошаговую инструкцию приготовления. "
                            +
                            "Ответ должен быть на русском языке и содержать только рецепты без дополнительных комментариев.",
                    ingredients);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAIConfig.getModel())
                    .messages(List.of(new ChatMessage("user", prompt)))
                    .maxTokens(openAIConfig.getMaxTokens())
                    .build();

            String response = service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
            log.info("Generated recipes for ingredients: {}", ingredients);
            return response;

        } catch (Exception e) {
            log.error("Error generating recipes for ingredients: {}", ingredients, e);
            return "Извините, произошла ошибка при генерации рецептов. Попробуйте еще раз.";
        }
    }
}