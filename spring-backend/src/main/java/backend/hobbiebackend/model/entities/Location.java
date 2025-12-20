package backend.hobbiebackend.model.entities;

import java.io.Serializable;

import backend.hobbiebackend.model.entities.enums.LocationEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Index;


@Entity
@Table(name = "locations", indexes = {
        @Index(name = "idx_locations_name", columnList = "name")
})
public class Location extends BaseEntity {

    private LocationEnum name;

    public Location(LocationEnum locationEnum) {
        this.name = locationEnum;
    }

    public Location() {
    }

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    public LocationEnum getName() {
        return name;
    }

    public void setName(LocationEnum name) {
        this.name = name;
    }
}
