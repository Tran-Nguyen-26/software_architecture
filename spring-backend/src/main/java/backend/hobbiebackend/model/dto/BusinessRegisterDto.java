package backend.hobbiebackend.model.dto;

import jakarta.validation.constraints.NotBlank;

public class BusinessRegisterDto {

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "businessName must be not blank")
    private String businessName;

    @NotBlank(message = "address must be not blank")
    private String address;

    @NotBlank(message = "email must be not blank")
    private String email;

    @NotBlank(message = "email must be not blank")
    private String password;

    public BusinessRegisterDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
