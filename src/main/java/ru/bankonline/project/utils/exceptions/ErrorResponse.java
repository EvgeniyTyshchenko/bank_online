package ru.bankonline.project.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.DTO;

import java.util.stream.Collectors;

/***
 * Класс для формирования ответа об ошибке
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private long timestamp;

    /***
     * Проверяет наличие ошибок при создании объекта
     * @param bindingResult результат проверки
     * @param validator валидатор данных
     * @param dto объект передачи данных
     * @throws NotCreatedException исключение, возникающее при ошибке создания объекта
     */
    public static void checkIfThereErrorInTheCreation(BindingResult bindingResult, Validator validator, DTO dto) {
        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = getErrorMessage(bindingResult);
            throw new NotCreatedException(errorMessage);
        }
    }

    /***
     * Проверяет наличие ошибок при обновлении объекта
     * @param bindingResult результат проверки
     * @param validator валидатор данных
     * @param dto объект передачи данных
     * @throws NotUpdatedException исключение, возникающее при ошибке обновления объекта
     */
    public static void checkIfThereErrorInTheUpdate(BindingResult bindingResult, Validator validator, DTO dto) {
        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = getErrorMessage(bindingResult);
            throw new NotUpdatedException(errorMessage);
        }
    }

    /***
     * Получает сообщение об ошибке из результата проверки
     * @param bindingResult результат проверки
     * @return сообщение об ошибке
     */
    public static String getErrorMessage(BindingResult bindingResult) {
        /*
         * Пример использования StreamApi
         */
        return bindingResult.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(
                        Collectors.joining(
                                "; "
                        ));
    }
}