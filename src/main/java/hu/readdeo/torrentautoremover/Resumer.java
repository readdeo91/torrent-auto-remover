package hu.readdeo.torrentautoremover;

import hu.readdeo.torrentautoremover.client.qbittorrent.QBittorrentClient;
import hu.readdeo.torrentautoremover.model.Torrent;
import hu.readdeo.torrentautoremover.model.TorrentList;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Resumer {

    private final QBittorrentClient client;

    @Value("${torrent.resume.threshhold.hours.before}")
    private int resumeThreshholdHours;

    public void resumeTorrents(TorrentList torrents) {
        log.info("Resuming torrents");
        TorrentList torrentsToResume = getTorrentsToResume(torrents);
        String hashes = getHashes(torrentsToResume);
        client.resumeTorrents(hashes);
    }

    private String getHashes(TorrentList torrents) {
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

    private TorrentList getTorrentsToResume(TorrentList torrents) {
        TorrentList torrentsToResume = new TorrentList();
        torrents.getTorrents()
                .forEach(
                        (torrent) -> {
                            addTorrentToNotRemoveList(torrent, torrentsToResume);
                        });
        return torrentsToResume;
    }

    private void addTorrentToNotRemoveList(Torrent torrent, TorrentList torrents) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(resumeThreshholdHours);
        if (torrent.getAddedOn().isAfter(thresholdTime)) {
            log.debug("Resuming torrent: {}", torrent.getName());
            torrents.add(torrent);
        }
    }
}
