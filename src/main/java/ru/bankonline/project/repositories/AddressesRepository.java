package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Address;

import java.util.List;

@Repository
public interface AddressesRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT c.address FROM Customer c")
    List<Address> findByAddresses();
}
