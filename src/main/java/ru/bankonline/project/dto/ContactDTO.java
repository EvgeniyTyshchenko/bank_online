package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.bankonline.project.entity.Contact;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
public class ContactDTO implements DTO {

    private String phoneNumber;
    private String email;

    public static Contact convertToContact(ContactDTO contactDTO, ModelMapper modelMapper) {
        return modelMapper.map(contactDTO, Contact.class);
    }

    public static ContactDTO convertToContactDTO(Contact contact, ModelMapper modelMapper) {
        return modelMapper.map(contact, ContactDTO.class);
    }

    public static List<ContactDTO> convertListContactDetailsToDTO(List<Contact> contacts, ModelMapper modelMapper) {
        List<ContactDTO> contactDTOs = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactDTO contactDTO = new ContactDTO();
            modelMapper.map(contact, contactDTO);
            contactDTOs.add(contactDTO);
        }
        return contactDTOs;
    }
}