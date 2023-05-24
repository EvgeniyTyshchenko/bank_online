package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.constants.Status;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDTO implements DTO {

    private Integer passportSeries;
    private Integer passportNumber;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String birthday;
    private AddressDTO addressDTO;
    private ContactDTO contactDTO;
    private Status status;

    private List<CardDTO> cardDTO;

    private List<SavingsAccountDTO> savingsAccountDTO;

    public CustomerDTO(Integer passportSeries, Integer passportNumber, String lastName, String firstName,
                       String patronymic, String birthday, AddressDTO addressDTO, ContactDTO contactDTO) {
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.addressDTO = addressDTO;
        this.contactDTO = contactDTO;
    }

    public static Customer convertToCustomerWithAddressAndContacts(CustomerDTO customerDTO, ModelMapper modelMapper) {
        return new Customer(customerDTO.passportSeries, customerDTO.passportNumber,
                customerDTO.lastName, customerDTO.firstName, customerDTO.patronymic,
                customerDTO.birthday, AddressDTO.convertToAddress(customerDTO.getAddressDTO(), modelMapper),
                ContactDTO.convertToContact(customerDTO.getContactDTO(),modelMapper));
    }

    public static Customer convertToCustomer(CustomerDTO customerDTO) {
        return new Customer(customerDTO.passportSeries, customerDTO.passportNumber, customerDTO.lastName,
                customerDTO.firstName, customerDTO.patronymic, customerDTO.birthday);
    }

    public static CustomerDTO convertToDTOTheEntireCustomerAndCardsAndAccounts(Customer customer, ModelMapper modelMapper) {
        return new CustomerDTO(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getLastName(), customer.getFirstName(), customer.getPatronymic(),
                customer.getBirthday(), AddressDTO.convertToAddressDTO(customer.getAddress(), modelMapper),
                ContactDTO.convertToContactDTO(customer.getContactDetails(), modelMapper), customer.getStatus(),
                CardDTO.convertListCardsToDTO(customer.getCards(), modelMapper),
                SavingsAccountDTO.convertSavingsAccountToDTO(customer.getSavingsAccounts(), modelMapper));
    }

    public static CustomerDTO convertToDTOCustomerWithAddressAndContacts(Customer customer, ModelMapper modelMapper) {
        return new CustomerDTO(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getLastName(), customer.getFirstName(), customer.getPatronymic(),
                customer.getBirthday(), AddressDTO.convertToAddressDTO(customer.getAddress(), modelMapper),
                ContactDTO.convertToContactDTO(customer.getContactDetails(), modelMapper));
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "passportSeries=" + passportSeries +
                ", passportNumber=" + passportNumber +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday='" + birthday + '\'' +
                ", addressDTO=" + addressDTO +
                ", contactDTO=" + contactDTO +
                '}';
    }
}