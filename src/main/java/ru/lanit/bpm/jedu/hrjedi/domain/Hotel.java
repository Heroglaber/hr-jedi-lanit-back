package ru.lanit.bpm.jedu.hrjedi.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "HOTEL")
public class Hotel implements Serializable {
    private static final long serialVersionUID = -5449326074498334532L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_id_generator")
    @SequenceGenerator(name = "hotel_id_generator", sequenceName = "sq_hotel_id", allocationSize = 1)
    private Long id;
    private String name;
    private Integer price;

    public Hotel() {
    }

    public Hotel(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

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
