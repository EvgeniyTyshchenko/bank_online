package ru.bankonline.project.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.DTO;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private long timestamp;

    public static void checkIfThereErrorInTheCreation(BindingResult bindingResult, Validator validator, DTO dto) {
        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = getErrorMessage(bindingResult);
            throw new NotCreatedException(errorMessage);
        }
    }

    public static void checkIfThereErrorInTheUpdate(BindingResult bindingResult, Validator validator, DTO dto) {
        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = getErrorMessage(bindingResult);
            throw new NotUpdatedException(errorMessage);
        }
    }

    public static String getErrorMessage(BindingResult bindingResult) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }
        return String.join("; ", errorMessages);
    }
}