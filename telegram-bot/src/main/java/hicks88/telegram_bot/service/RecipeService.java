package hicks88.telegram_bot.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
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

            String prompt = """
                            Бот-повар. Правила:
                            Если в «%s» есть слово вне списка съедобных (овощи, фрукты, ягоды, грибы, мясо, рыба, яйца, молоко, зерно, бобовые, орехи, семена), верни ровно:
                            ❌ Ошибка: <несъедобные>
                            Иначе создай 3 рецепта ТОЛЬКО из указанных (+соль, перец, вода, масло):
                            №1 Название:…
                            Ингредиенты:…
                            Шаги:1)…2)…3)
                            №2…
                            №3…
                            """ .formatted(ingredients);





            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAIConfig.getModel())
                    .messages(List.of(new ChatMessage("user", prompt)))
                    .maxTokens(openAIConfig.getMaxTokens())
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent();

            if (result.getUsage() != null) {
                log.info(
                        "OpenAI token usage - prompt: {}, completion: {}, total: {}",
                        result.getUsage().getPromptTokens(),
                        result.getUsage().getCompletionTokens(),
                        result.getUsage().getTotalTokens()
                );
            }
            log.info("Generated recipes for ingredients: {}", ingredients);
            return response;

        } catch (Exception e) {
            log.error("Error generating recipes for ingredients: {}", ingredients, e);
            return "Извините, произошла ошибка при генерации рецептов. Попробуйте еще раз.";
        }
    }
}