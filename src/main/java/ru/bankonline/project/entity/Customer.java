package ru.bankonline.project.entity;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;
import ru.bankonline.project.constants.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    @Column(name = "passport_series")
    private Integer passportSeries;

    @Column(name = "passport_number")
    private Integer passportNumber;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "birthday")
    private String birthday;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", referencedColumnName = "contact_id")
    private Contact contactDetails;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_date_time",
            columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createDate;

    @Column(name = "update_date_time",
            columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updateDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private List<Card> cards;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private List<SavingsAccount> savingsAccounts;

    public Customer(Integer passportSeries, Integer passportNumber, String lastName, String firstName,
                    String patronymic, String birthday, Address address, Contact contactDetails) {
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.address = address;
        this.contactDetails = contactDetails;
    }

    public Customer(Integer passportSeries, Integer passportNumber, String lastName, String firstName,
                    String patronymic, String birthday, Address address, Contact contactDetails, List<Card> cards,
                    List<SavingsAccount> savingsAccounts) {
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.address = address;
        this.contactDetails = contactDetails;
        this.cards = cards;
        this.savingsAccounts = savingsAccounts;
    }

    public Customer(Integer customerId, Integer passportSeries, Integer passportNumber, String lastName,
                    String firstName, String patronymic, String birthday, Address address, Contact contactDetails) {
        this.customerId = customerId;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.address = address;
        this.contactDetails = contactDetails;
    }
}