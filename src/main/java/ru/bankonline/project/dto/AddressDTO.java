package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.bankonline.project.entity.Address;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
public class AddressDTO extends DTO {

    private String country;
    private String city;
    private String street;
    private String house;
    private Integer apartment;

    public static Address convertToAddress(AddressDTO addressDTO, ModelMapper modelMapper) {
        return modelMapper.map(addressDTO, Address.class);
    }

    public static AddressDTO convertToAddressDTO(Address address, ModelMapper modelMapper) {
        return modelMapper.map(address, AddressDTO.class);
    }

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