package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.SavingsAccount;

import java.util.List;

@Repository
public interface SavingsAccountsRepository extends JpaRepository<SavingsAccount, Integer> {

    List<SavingsAccount> findByCustomerId(Integer customerId);
}
