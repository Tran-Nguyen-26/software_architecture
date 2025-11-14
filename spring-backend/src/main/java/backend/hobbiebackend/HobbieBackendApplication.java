package backend.hobbiebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HobbieBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HobbieBackendApplication.class, args);
    }

}
