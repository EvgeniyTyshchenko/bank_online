package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Contact;

/***
 * Репозиторий для работы с контактами
 */
@Repository
public interface ContactsRepository extends JpaRepository<Contact, Integer> {
}