package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Contact;

@Repository
public interface ContactsRepository extends JpaRepository<Contact, Integer> {

    Contact findByEmail(String email);
}
