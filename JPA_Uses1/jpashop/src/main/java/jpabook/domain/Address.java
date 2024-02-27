package jpabook.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcod;

    protected Address() {
    }

    public Address(String city, String street, String zipcod) {
        this.city = city;
        this.street = street;
        this.zipcod = zipcod;
    }
}
