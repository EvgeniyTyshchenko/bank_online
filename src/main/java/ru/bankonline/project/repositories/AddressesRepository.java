package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Address;

/***
 * Репозиторий для работы с адресами
 */
@Repository
public interface AddressesRepository extends JpaRepository<Address, Integer> {
}