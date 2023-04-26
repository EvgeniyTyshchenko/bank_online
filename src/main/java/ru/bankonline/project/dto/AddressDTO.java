package ru.bankonline.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.bankonline.project.entity.Address;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @Override
    public String toString() {
        return "AddressDTO{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", apartment=" + apartment +
                '}';
    }
}