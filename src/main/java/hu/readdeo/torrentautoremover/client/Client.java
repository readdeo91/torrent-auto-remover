package hu.readdeo.torrentautoremover.client;

import hu.readdeo.torrentautoremover.model.TorrentList;

public interface Client {
    TorrentList getTorrentList();

    void removeTorrents(TorrentList torrentsToDelete);

    void resumeTorrents(String hashes);
}
