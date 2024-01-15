package hu.readdeo.torrentautoremover;

import hu.readdeo.torrentautoremover.client.qbittorrent.QBittorrentClient;
import hu.readdeo.torrentautoremover.model.TorrentList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Runner {

    private final Remover remover;
    private final Resumer resumer;
    private final QBittorrentClient client;

    @Value("${torrent.resume.enable}")
    private boolean resumeEnabled;

    public void run() {
        TorrentList torrentsFromCategory = client.getTorrentList();
        remover.remove(torrentsFromCategory);
        if (resumeEnabled) resumer.resumeTorrents(torrentsFromCategory);
    }
}
