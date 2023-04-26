package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Card;

import java.util.List;

@Repository
public interface CardsRepository extends JpaRepository<Card, Integer> {

    List<Card> findByCustomerId(Integer customerId);
}
