package hu.readdeo.torrentautoremover;

import hu.readdeo.torrentautoremover.remover.Remover;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class TorrentAutoremoverApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(TorrentAutoremoverApplication.class, args);
        final Remover remover = context.getBean(Remover.class);
        remover.run();
    }
}
