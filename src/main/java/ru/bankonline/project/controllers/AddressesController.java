package ru.bankonline.project.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.services.addressesservice.AddressesService;
import ru.bankonline.project.utils.validators.AddressValidator;

import java.util.List;

import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheUpdate;

@RestController
@RequestMapping("/addresses")
public class AddressesController {

    private final AddressesService addressesService;
    private final AddressValidator addressValidator;
    private final ModelMapper modelMapper;

    public AddressesController(AddressesService addressesService, AddressValidator addressValidator, ModelMapper modelMapper) {
        this.addressesService = addressesService;
        this.addressValidator = addressValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(AddressDTO.convertListAddressesToDTO(addressesService.getAllCustomerAddresses(),
                modelMapper));
    }

    @PutMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateAddress(@PathVariable Integer series, @PathVariable Integer number,
                                                    @RequestBody AddressDTO addressDTO, BindingResult bindingResult) {
        checkIfThereErrorInTheUpdate(bindingResult, addressValidator, addressDTO);
        addressesService.updateAddress(series, number, AddressDTO.convertToAddress(addressDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
