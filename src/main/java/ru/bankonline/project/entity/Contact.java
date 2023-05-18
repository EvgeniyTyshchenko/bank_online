package ru.bankonline.project.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Contact {

    @Id
    @Column(name = "contact_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    public Contact(String phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}