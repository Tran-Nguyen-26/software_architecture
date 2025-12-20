package backend.hobbiebackend.model.entities;

import java.io.Serializable;

import backend.hobbiebackend.model.entities.enums.CategoryNameEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_name", columnList = "name")
})
public class Category extends BaseEntity {
    private CategoryNameEnum name;

    public Category(CategoryNameEnum categoryNameEnum) {
        this.name = categoryNameEnum;
    }

    public Category() {
    }

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    public CategoryNameEnum getName() {
        return name;
    }

    public void setName(CategoryNameEnum name) {
        this.name = name;
    }
}
