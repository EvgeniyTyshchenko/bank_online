package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.services.contactsservice.ContactsService;
import ru.bankonline.project.utils.validators.ContactValidator;

import java.util.List;

import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheUpdate;

@Slf4j
@RestController
@RequestMapping("/contacts")
@Tag(name = "Контакты", description = "CRUD-операции для работы с контактами")
public class ContactsController {

    private final ContactsService contactsService;
    private final ContactValidator contactValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public ContactsController(ContactsService contactsService, ContactValidator contactValidator, ModelMapper modelMapper) {
        this.contactsService = contactsService;
        this.contactValidator = contactValidator;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Получение всех контактных данных клиентов банка")
    @GetMapping("/getAll")
    public ResponseEntity<List<ContactDTO>> getAllContactDetails() {
        return ResponseEntity.ok(ContactDTO.convertListContactDetailsToDTO(contactsService.getAllCustomerContactsDetails(),
                modelMapper));
    }

    @Operation(summary = "Обновление контактных данных клиента",
            description = "Необходимо вводить серию и номер паспорта клиента, у которого необходимо обновить " +
                    "информацию, далее, заполнить поля в формате JSON")
    @PutMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateContactDetails(@PathVariable Integer series, @PathVariable Integer number,
                                                           @RequestBody ContactDTO contactDTO, BindingResult bindingResult) {
        log.info("Обновление контактов в базе по серии {} и номеру {} паспорта", series, number);
        checkIfThereErrorInTheUpdate(bindingResult, contactValidator, contactDTO);
        contactsService.updateContactsDetails(series, number, ContactDTO.convertToContact(contactDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}