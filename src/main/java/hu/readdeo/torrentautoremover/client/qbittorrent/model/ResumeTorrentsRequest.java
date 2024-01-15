package hu.readdeo.torrentautoremover.client.qbittorrent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeTorrentsRequest {
    private String hashes;
}
