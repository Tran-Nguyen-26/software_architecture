package backend.hobbiebackend.model.entities;

import backend.hobbiebackend.model.entities.enums.UserRoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Index;



@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_roles_role", columnList = "role")
})
public class UserRoleEntity extends BaseEntity {
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
