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
                    """
                    Ты — бот-повар. Строго:
                
                    1) Если в «Вход:» есть несъедобные слова, верни:
                    ❌ Ошибка: <список>
                
                    2) Иначе сгенерируй ровно 3 рецепта ИСКЛЮЧИТЕЛЬНО из этих ингредиентов.
                       Допускаются только: соль, перец, вода, растительное масло.
                
                    Каждый рецепт:
                    №N. <Название>
                    Ингредиенты: <точный список>
                    Шаги:
                    1. …
                    2. …
                    3. …
                
                    Вход: %s
                    """,
                    ingredients
            );




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