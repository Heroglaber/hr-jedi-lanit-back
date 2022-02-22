package ru.lanit.bpm.jedu.hrjedi.domain;

import javax.persistence.*;

@Entity
@Table(name = "HOTEL")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_id_generator")
    @SequenceGenerator(name = "hotel_id_generator", sequenceName = "sq_hotel_id", allocationSize = 1)
    private Long id;
    private String name;
    private Integer price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
