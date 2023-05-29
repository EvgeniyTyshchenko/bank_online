package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;

import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.constants.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * Класс, представляющий DTO (Data Transfer Object) для сберегательного счета
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SavingsAccountDTO implements DTO {

    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;

    /***
     * Преобразует список объектов SavingsAccount в список объектов SavingsAccountDTO с помощью ModelMapper
     * @param savingsAccounts список объектов SavingsAccount
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return список объектов SavingsAccountDTO
     */
    public static List<SavingsAccountDTO> convertListSavingsAccountToDTO(List<SavingsAccount> savingsAccounts,
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