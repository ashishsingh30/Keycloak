package au.com.cliquote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "au.com.cliquote")  // Ensure all your packages are scanned for Spring components
public class CliQuoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CliQuoteApplication.class, args);
    }
}
