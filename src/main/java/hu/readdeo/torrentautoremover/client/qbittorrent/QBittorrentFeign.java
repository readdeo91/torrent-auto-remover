package hu.readdeo.torrentautoremover.client.qbittorrent;

import feign.Param;
import hu.readdeo.torrentautoremover.client.FeignConfig;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.DeleteTorrentsRequest;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.GetTorrentsRequest;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        value = "qbittorrent-client",
        url = "${qbittorrent.url}",
        configuration = FeignConfig.class)
public interface QBittorrentFeign {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/api/v2/auth/login",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> login(
            @RequestBody LoginRequest loginRequest,
            @RequestHeader(value = "Content-type", defaultValue = "multipart/form-data")
                    String contentType);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/v2/torrents/info",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getTorrents(
            @RequestBody GetTorrentsRequest getTorrentsRequest,
            @RequestHeader(value = "Cookie") String cookie);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/api/v2/torrents/delete",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String deleteTorrents(
            @RequestBody DeleteTorrentsRequest deleteTorrentsRequest,
            @RequestHeader(
                            value = "Content-type",
                            required = false,
                            defaultValue = "application/x-www-form-urlencoded; charset=UTF-8")
                    String contentType,
            @RequestHeader(value = "Cookie") String cookie);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/v2/torrents/resume?hash={torrentHashes}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String resumetorrents(
            @Param("torrentHashes") String torrentHashes);
}
