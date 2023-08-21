package hu.readdeo.torrentautoremover.remover.model;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;

@Data
public class TorrentList {
    List<Torrent> torrents = new LinkedList<>();

    public boolean add(Torrent torrent) {
        return torrents.add(torrent);
    }
}
