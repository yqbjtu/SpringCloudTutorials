package hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import hello.storage.StorageProperties;
import hello.storage.StorageService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(StorageProperties.class)
public class FileUploadDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
