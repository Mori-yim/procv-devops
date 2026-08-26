package cm.procv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ProcvApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcvApplication.class, args);
    }
}
