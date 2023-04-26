package ru.bankonline.project.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.services.contactsservice.ContactsService;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

    private final ContactsService contactsService;
    private final ModelMapper modelMapper;

    public ContactsController(ContactsService contactsService, ModelMapper modelMapper) {
        this.contactsService = contactsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ContactDTO> getContactByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ContactDTO.convertToContactDTO(contactsService.searchContactByEmail(email),
                modelMapper));
    }
}