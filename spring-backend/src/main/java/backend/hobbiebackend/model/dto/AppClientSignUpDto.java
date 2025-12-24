package backend.hobbiebackend.model.dto;

import backend.hobbiebackend.model.entities.enums.GenderEnum;
import jakarta.validation.constraints.NotBlank;

public class AppClientSignUpDto {

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "fullname must be not blank")
    private String fullName;

    private GenderEnum gender;

    @NotBlank(message = "email must be not blank")
    private String email;

    @NotBlank(message = "password must be not blank")
    private String password;

    public AppClientSignUpDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
