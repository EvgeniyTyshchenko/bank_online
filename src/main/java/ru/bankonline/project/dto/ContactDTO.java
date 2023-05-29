package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Contact;

import java.util.ArrayList;
import java.util.List;

/***
 * Класс, представляющий DTO (Data Transfer Object) для контактов
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ContactDTO implements DTO {

    private String phoneNumber;
    private String email;

    /***
     * Преобразует объект ContactDTO в объект Contact с помощью ModelMapper
     * @param contactDTO объект ContactDTO
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return объект Contact
     */
    public static Contact convertToContact(ContactDTO contactDTO, ModelMapper modelMapper) {
        return modelMapper.map(contactDTO, Contact.class);
    }

    /***
     * Преобразует объект Contact в объект ContactDTO с помощью ModelMapper
     * @param contact объект Contact
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return объект ContactDTO
     */
    public static ContactDTO convertToContactDTO(Contact contact, ModelMapper modelMapper) {
        return modelMapper.map(contact, ContactDTO.class);
    }

    /***
     * Преобразует список объектов Contact в список объектов ContactDTO с помощью ModelMapper
     * @param contacts список объектов Contact
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return список объектов ContactDTO
     */
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