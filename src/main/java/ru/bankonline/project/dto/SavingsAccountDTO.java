package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;

import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.constants.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SavingsAccountDTO implements DTO {

    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;

    public static List<SavingsAccountDTO> convertSavingsAccountToDTO(List<SavingsAccount> savingsAccounts,
                                                                     ModelMapper modelMapper) {
        List<SavingsAccountDTO> savingsAccountDTOs = new ArrayList<>();
        for (SavingsAccount savingsAccount : savingsAccounts) {
            SavingsAccountDTO savingsAccountDTO = new SavingsAccountDTO();
            modelMapper.map(savingsAccount, savingsAccountDTO);
            savingsAccountDTOs.add(savingsAccountDTO);
        }
        return savingsAccountDTOs;
    }
}