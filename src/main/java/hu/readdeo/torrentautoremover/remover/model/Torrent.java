package hu.readdeo.torrentautoremover.remover.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Torrent {
    @Getter @Setter private String data;
    @Getter @Setter private String name;
    @Getter private LocalDateTime addedOn;
}
