package hicks88.telegram_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ValidationService {

    /**
     * Валидирует список ингредиентов
     * 
     * @param ingredients строка с ингредиентами через запятую
     * @return результат валидации
     */
    public ValidationResult validateIngredients(String ingredients) {
        if (ingredients == null || ingredients.trim().isEmpty()) {
            return new ValidationResult(false, "Пожалуйста, укажите ингредиенты.");
        }

        String trimmedIngredients = ingredients.trim();

        if (trimmedIngredients.length() < 3) {
            return new ValidationResult(false, "Список ингредиентов слишком короткий. Укажите больше ингредиентов.");
        }

        if (trimmedIngredients.length() > 500) {
            return new ValidationResult(false, "Список ингредиентов слишком длинный. Укажите меньше ингредиентов.");
        }

        // Проверяем, что есть запятые (хотя бы один ингредиент)
        if (!trimmedIngredients.contains(",")) {
            return new ValidationResult(false, "Пожалуйста, укажите несколько ингредиентов через запятую.");
        }

        // Разбиваем на ингредиенты и проверяем каждый
        List<String> ingredientList = Arrays.stream(trimmedIngredients.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (ingredientList.size() < 2) {
            return new ValidationResult(false, "Укажите минимум 2 ингредиента.");
        }

        if (ingredientList.size() > 20) {
            return new ValidationResult(false, "Слишком много ингредиентов. Укажите максимум 20 ингредиентов.");
        }

        // Проверяем каждый ингредиент
        for (String ingredient : ingredientList) {
            if (ingredient.length() < 2) {
                return new ValidationResult(false, "Ингредиент '" + ingredient + "' слишком короткий.");
            }
            if (ingredient.length() > 50) {
                return new ValidationResult(false, "Ингредиент '" + ingredient + "' слишком длинный.");
            }
            if (!ingredient.matches("^[а-яА-Яa-zA-Z0-9\\s\\-]+$")) {
                return new ValidationResult(false, "Ингредиент '" + ingredient + "' содержит недопустимые символы.");
            }
        }

        log.info("Ingredients validated successfully: {}", ingredientList);
        return new ValidationResult(true, null, ingredientList);
    }

    /**
     * Результат валидации
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final List<String> ingredients;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.ingredients = null;
        }

        public ValidationResult(boolean valid, String errorMessage, List<String> ingredients) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.ingredients = ingredients;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<String> getIngredients() {
            return ingredients;
        }
    }
}