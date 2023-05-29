package ru.bankonline.project.entity;

import lombok.*;

import javax.persistence.*;

/***
 * Класс, представляющий сущность "Адрес"
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "house")
    private String house;

    @Column(name = "apartment")
    private Integer apartment;

    /***
     * Конструктор для создания объекта Address
     * @param country страна
     * @param city город
     * @param street улица
     * @param house номер дома
     * @param apartment номер квартиры
     */
    public Address(String country, String city, String street, String house, Integer apartment) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
    }
}