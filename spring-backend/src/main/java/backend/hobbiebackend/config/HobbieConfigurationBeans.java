package backend.hobbiebackend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;


@Configuration
public class HobbieConfigurationBeans {
    @Bean
    public PasswordEncoder createPasswordEncoder() {
        // return new Pbkdf2PasswordEncoder(
        // "", // secret (để trống nếu không dùng)
        // 310000, // số vòng lặp (khuyến nghị >= 310000)
        // 256, // độ dài hash
        // Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }



}
