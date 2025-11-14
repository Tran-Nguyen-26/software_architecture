package backend.hobbiebackend.model.entities;

import java.io.Serializable;

import backend.hobbiebackend.model.entities.enums.UserRoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;



@Entity
@Table(name = "roles")
public class UserRoleEntity extends BaseEntity implements Serializable {
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
