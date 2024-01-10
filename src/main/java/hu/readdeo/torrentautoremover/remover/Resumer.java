package hu.readdeo.torrentautoremover.remover;

import hu.readdeo.torrentautoremover.client.qbittorrent.QBittorrentClient;
import hu.readdeo.torrentautoremover.remover.model.TorrentList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class Resumer {

    private final QBittorrentClient client;

    public void resumeTorrents(TorrentList torrents) {
        log.info("Resuming torrents");
        String hashes = getHashes(torrents);
        client.resumeTorrents(hashes);
    }

    private String getHashes (TorrentList torrents) {
        StringBuilder hashesBulder = new StringBuilder();
        torrents.getTorrents()
                .forEach(
                        (torrent) -> {
                            hashesBulder.append(torrent.getHash());
                            hashesBulder.append("|");
                        });
        hashesBulder.deleteCharAt(hashesBulder.length() - 1);
        return hashesBulder.toString();
    }
}
