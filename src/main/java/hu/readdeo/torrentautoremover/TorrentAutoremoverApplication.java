package hu.readdeo.torrentautoremover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TorrentAutoremoverApplication {

    public static void main(String[] args) {
        SpringApplication.run(TorrentAutoremoverApplication.class, args);
    }
}
