package backend.hobbiebackend.model.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not null")
    private String password;
}