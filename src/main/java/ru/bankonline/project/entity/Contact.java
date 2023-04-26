package ru.bankonline.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
@NoArgsConstructor
@Getter
@Setter
public class Contact {

    @Id
    @Column(name = "contact_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;
}