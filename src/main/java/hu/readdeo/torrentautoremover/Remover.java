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
public class Remover {

    private final QBittorrentClient client;

    @Value("${torrent.remove.threshold.days}")
    private int thresholdDays;

    @Value("${torrent.dry.run}")
    private boolean dryRun;

    public void remove(TorrentList torrents) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusDays(thresholdDays);
        log.trace("thresholdTime {}", thresholdTime);
        TorrentList torrentsToRemove = getRemovableTorrentList(torrents, thresholdTime);
        removeTorrents(torrentsToRemove);
        log.info("Finished removing torrents");
    }

    private static TorrentList getRemovableTorrentList(
            TorrentList torrents, LocalDateTime thresholdTime) {
        TorrentList torrentsToRemove = new TorrentList();
        torrents.getTorrents()
                .forEach(
                        (torrent) -> {
                            addTorrentToRemoveListIfThresholdExceeded(
                                    thresholdTime, torrent, torrentsToRemove);
                        });
        return torrentsToRemove;
    }

    private void removeTorrents(TorrentList torrentsToRemove) {
        if (!torrentsToRemove.getTorrents().isEmpty()) {
            log.info("Removing torrents");
            if (!dryRun) client.removeTorrents(torrentsToRemove);
        } else {
            log.info("Nothing to do.");
        }
    }

    private static void addTorrentToRemoveListIfThresholdExceeded(
            LocalDateTime thresholdTime, Torrent torrent, TorrentList torrentsToRemove) {
        if (torrent.getAddedOn().isBefore(thresholdTime)) {
            log.debug("Deleting torrent: {}", torrent.getName());
            torrentsToRemove.add(torrent);
        }
    }
}
