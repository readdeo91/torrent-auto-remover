# Torrent auto remover

This application is removing torrents from your torrent client that was added earlier than the specified days.
By default, the application is removing torrents that are older than 14 days and deletes the files. The application currently only supports qBittorrent.

## Running the application
- Build it with bootjar.
- Create application.properties file next to the jar.
- Edit the properties to fit your needs, shown below.
- start it with java -jar torrent-auto-remover.jar

### application.properties file content
By default, the application is using the configuration shown below. You can skip the properties file if it is good for you.
```properties
qbittorrent.url=http://localhost:8080
qbittorrent.username=admin
qbittorrent.password=adminadmin
qbittorrent.delete.files=true

torrent.remove.threshold.days=14
torrent.category=AUTO_REMOVE
torrent.dry.run=false
```
