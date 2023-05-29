package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Address;

import java.util.ArrayList;
import java.util.List;

/***
 * Класс, представляющий DTO (Data Transfer Object) для адреса
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddressDTO implements DTO {

    private String country;
    private String city;
    private String street;
    private String house;
    private Integer apartment;

    /***
     * Преобразует объект AddressDTO в объект Address с помощью ModelMapper
     * @param addressDTO объект AddressDTO
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return объект Address
     */
    public static Address convertToAddress(AddressDTO addressDTO, ModelMapper modelMapper) {
        return modelMapper.map(addressDTO, Address.class);
    }

    /***
     * Преобразует объект Address в объект AddressDTO с помощью ModelMapper
     * @param address объект Address
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return объект AddressDTO
     */
    public static AddressDTO convertToAddressDTO(Address address, ModelMapper modelMapper) {
        return modelMapper.map(address, AddressDTO.class);
    }

    /***
     * Преобразует список объектов Address в список объектов AddressDTO с помощью ModelMapper
     * @param addresses список объектов Address
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return список объектов AddressDTO
     */
    public static List<AddressDTO> convertListAddressesToDTO(List<Address> addresses, ModelMapper modelMapper) {
        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (Address address : addresses) {
            AddressDTO addressDTO = new AddressDTO();
            modelMapper.map(address, addressDTO);
            addressDTOs.add(addressDTO);
        }
        return addressDTOs;
    }
}