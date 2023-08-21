package hu.readdeo.torrentautoremover.client.qbittorrent.model;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}
