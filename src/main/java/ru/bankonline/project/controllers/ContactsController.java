package ru.bankonline.project.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.services.contactsservice.ContactsService;
import ru.bankonline.project.utils.validators.ContactValidator;

import java.util.List;

import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheUpdate;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

    private final ContactsService contactsService;
    private final ContactValidator contactValidator;
    private final ModelMapper modelMapper;

    public ContactsController(ContactsService contactsService, ContactValidator contactValidator, ModelMapper modelMapper) {
        this.contactsService = contactsService;
        this.contactValidator = contactValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ContactDTO>> getAllContactDetails() {
        return ResponseEntity.ok(ContactDTO.convertListContactDetailsToDTO(contactsService.getAllCustomerContactsDetails(),
                modelMapper));
    }

    @PutMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateContactDetails(@PathVariable Integer series, @PathVariable Integer number,
                                                           @RequestBody ContactDTO contactDTO, BindingResult bindingResult) {
        checkIfThereErrorInTheUpdate(bindingResult, contactValidator, contactDTO);
        contactsService.updateContactsDetails(series, number, ContactDTO.convertToContact(contactDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}