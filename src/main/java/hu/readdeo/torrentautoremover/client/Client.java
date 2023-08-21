package hu.readdeo.torrentautoremover.client;

import hu.readdeo.torrentautoremover.remover.model.TorrentList;

public interface Client {
    TorrentList getTorrentList();

    void removeTorrents(TorrentList torrentsToDelete);
}
