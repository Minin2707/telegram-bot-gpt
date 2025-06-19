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
                    Ты — ассистент-повар. Построй логику по следующему сценарию:
                
                    1) Проверь входной список ингредиентов (они идут после двоеточия).
                       Если найдётся хоть одно несъедобное слово (например «солнце», «камень», «ножницы»),
                       верни ровно:
                       ❌ Запрос содержит несъедобные позиции: <список ошибочных слов>
                       и _никаких_ рецептов.
                
                    2) Иначе, если все слова съедобны, сгенерируй ровно 3 рецепта,
                       **исключительно** из перечисленных ингредиентов.
                       Ни один рецепт **не должен** содержать никаких других продуктов.
                       При этом:
                         • Не вводи новых ингредиентов (кроме тех, что были в списке).
                         • Если рецепт требует масла, воды, соли или специй —  упоминай их.
                       Для каждого рецепта формат такой:
                         Рецепт №N
                         Название: <название>
                         Шаги:
                         1. …
                         2. …
                         3. …
                
                    Ответ должен быть на русском языке, без лишних вступлений и прощаний.
                
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