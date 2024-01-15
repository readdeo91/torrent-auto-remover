package hu.readdeo.torrentautoremover.client.qbittorrent;

import hu.readdeo.torrentautoremover.client.Client;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.DeleteTorrentsRequest;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.GetTorrentsRequest;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.LoginRequest;
import hu.readdeo.torrentautoremover.client.qbittorrent.model.ResumeTorrentsRequest;
import hu.readdeo.torrentautoremover.model.Torrent;
import hu.readdeo.torrentautoremover.model.TorrentList;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class QBittorrentClient implements Client {

    private final QBittorrentFeign client;

    @Value("${torrent.category}")
    private String category;

    @Value("${qbittorrent.username}")
    private String userName;

    @Value("${qbittorrent.password}")
    private String password;

    @Value("${qbittorrent.delete.files}")
    private boolean deleteFiles;

    @Value("${qbittorrent.url}")
    private String url;

    private String cookie;
    private String contentType = "application/x-www-form-urlencoded; charset=UTF-8";

    public TorrentList getTorrentList() {
        authorizeIfNeeded();
        JSONArray torrentsJson = callGetTorrentsRequest();
        return covnertJSONArrayToTorrentList(torrentsJson);
    }

    private TorrentList covnertJSONArrayToTorrentList(JSONArray torrentsJson) {
        TorrentList torrentList = new TorrentList();
        for (int i = 0; i < torrentsJson.length(); i++) {
            JSONObject torrentJson = getTorrent(torrentsJson, i);
            LocalDateTime addedOnTime = getAddedOnTime(torrentJson);
            String name = getTorrentName(torrentJson);
            String hash = getHash(torrentJson);
            Torrent torrent = new Torrent(torrentJson.toString(), name, hash, addedOnTime);
            torrentList.add(torrent);
        }
        return torrentList;
    }

    public void removeTorrents(TorrentList torrents) {
        String removableTorrentHashes = getTorrentsToRemove(torrents);
        log.debug("removableTorrentHashes: {}", removableTorrentHashes);
        sendDeleteTorrentsRequest(removableTorrentHashes);
    }

    @Override
    public void resumeTorrents(String hashes) {
        ResumeTorrentsRequest resumeTorrentsRequest = new ResumeTorrentsRequest();
        resumeTorrentsRequest.setHashes(hashes);
        client.resumeTorrents(resumeTorrentsRequest, contentType, cookie);
    }

    private String getTorrentName(JSONObject torrent) {
        try {
            return torrent.getString("name");
        } catch (JSONException e) {
            log.error("Failed to get name of torrent: {}", e.toString());
            throw new RuntimeException(e);
        }
    }

    private JSONArray callGetTorrentsRequest() {
        GetTorrentsRequest request = new GetTorrentsRequest(category);
        String response = client.getTorrents(request, cookie);
        log.trace("getTorrentsList response: {}", response);
        return getJsonArrayFromResponse(response);
    }

    private static LocalDateTime getAddedOnTime(JSONObject torrent) {
        long addedOn = 0;
        try {
            addedOn = torrent.getLong("added_on");
        } catch (JSONException e) {
            log.error("Failed to extract added_on from torrent json.");
            throw new RuntimeException(e);
        }
        LocalDateTime addedOnTime =
                LocalDateTime.ofEpochSecond(addedOn, 0, OffsetDateTime.now().getOffset());
        log.trace("addedOnTime {}", addedOnTime);
        return addedOnTime;
    }

    private void sendDeleteTorrentsRequest(String removableTorrentHashes) {
        DeleteTorrentsRequest deleteTorrentsRequest =
                new DeleteTorrentsRequest(removableTorrentHashes, deleteFiles);
        client.deleteTorrents(
                deleteTorrentsRequest, contentType, cookie);
    }

    private String getTorrentsToRemove(TorrentList torrents) {
        StringBuilder stringBuilder = new StringBuilder();
        torrents.getTorrents()
                .forEach(
                        (torrent) -> {
                            try {
                                JSONObject torrentJson = new JSONObject(torrent.getData());
                                String hash = getHash(torrentJson);
                                if (!stringBuilder.isEmpty()) {
                                    stringBuilder.append("|");
                                }
                                stringBuilder.append(hash);
                            } catch (JSONException e) {
                                log.error("Failed to parse JSON from torrent: {}", e.toString());
                                throw new RuntimeException(e);
                            }
                        });
        return stringBuilder.toString();
    }

    private static String getHash(JSONObject torrent) {
        String hash = null;
        try {
            hash = torrent.getString("hash");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return hash;
    }

    private JSONObject getTorrent(JSONArray torrents, int i) {
        try {
            return torrents.getJSONObject(i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONArray getJsonArrayFromResponse(String response) {
        JSONArray responseJsonArray = null;
        try {
            responseJsonArray = new JSONArray(response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        log.debug(responseJsonArray.toString());
        return responseJsonArray;
    }

    private void login() {
        log.info("Logging in to {}", url);
        ResponseEntity<String> loginResponse = sendLoginRequest();
        handleLoginResponse(loginResponse);
    }

    private void handleLoginResponse(ResponseEntity<String> loginResponse) {
        if (loginResponse.getHeaders().getFirst("set-cookie") != null) {
            cookie = loginResponse.getHeaders().getFirst("set-cookie");
            log.info("Login successful!");
        } else {
            log.error("Failed to login! loginResponse: {}", loginResponse);
            throw new RuntimeException();
        }
    }

    private ResponseEntity<String> sendLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(userName);
        loginRequest.setPassword(password);

        return client.login(loginRequest, "multipart/form-data");
    }

    private void authorizeIfNeeded() {
        if (!StringUtils.hasText(cookie)) {
            login();
        }
    }
}
