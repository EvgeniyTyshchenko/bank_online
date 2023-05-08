package ru.bankonline.project.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.CustomerDTO;

@Component
public class FullCustomerValidator implements Validator {
    private final AddressValidator addressValidator;
    private final ContactValidator contactValidator;
    private final CustomerValidator customerValidator;

    @Autowired
    public FullCustomerValidator(AddressValidator addressValidator, ContactValidator contactValidator, CustomerValidator customerValidator) {
        this.addressValidator = addressValidator;
        this.contactValidator = contactValidator;
        this.customerValidator = customerValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO customerDTO = (CustomerDTO) target;

        customerValidator.validate(customerDTO, errors);

        errors.pushNestedPath("addressDTO");
        ValidationUtils.invokeValidator(this.addressValidator, customerDTO.getAddressDTO(), errors);
        errors.popNestedPath();

        errors.pushNestedPath("contactDTO");
        ValidationUtils.invokeValidator(this.contactValidator, customerDTO.getContactDTO(), errors);
        errors.popNestedPath();
    }
}
