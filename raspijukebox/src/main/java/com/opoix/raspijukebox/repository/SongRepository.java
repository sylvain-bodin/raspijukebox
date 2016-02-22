package com.opoix.raspijukebox.repository;

import com.opoix.raspijukebox.config.ConfigurationManager;
import com.opoix.raspijukebox.entity.Song;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Created by copoix on 1/2/16.
 */
public class SongRepository {

    public static final Predicate<Path> MP3_FILENAME_FILTER =
            p -> p.getFileName().toString().endsWith(".mp3");
    private static final Object lock = new Object();
    private static SongRepository instance = new SongRepository();
    private static ConfigurationManager config = ConfigurationManager.getInstance();
    private Map<Long, Song> repository = new ConcurrentHashMap<>();
    private AtomicLong generator = new AtomicLong(0);

    public static SongRepository getInstance() {
        return SongRepository.instance;
    }


    public void scan() {
        synchronized (lock) {
            truncate();
            String songsDir = config.getProperty("song.path");
            Boolean recursive = Boolean.valueOf(config.getProperty("song.recursive.scan"));
            Integer depth = 1;
            if (recursive) {
                depth = Integer.MAX_VALUE;
            }
            try {
                Files.walk(new File(songsDir).toPath(), depth, FileVisitOption.FOLLOW_LINKS).filter(MP3_FILENAME_FILTER).forEach(path -> {
                    AudioFileFormat baseFileFormat = null;
                    try {
                        baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(path.toFile());
                    } catch (UnsupportedAudioFileException | IOException e) {
                        return;
                    }
                    Song song = buildSong(path, baseFileFormat);
                    save(song);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Song buildSong(Path path, AudioFileFormat baseFileFormat) {
        Map<String, Object> properties = baseFileFormat.properties();
        String track;
        String disc;
        Song.SongBuilder sb = Song.builder();

        sb.title((String) properties.get("title"));
        sb.artist((String) properties.get("author"));
        sb.album((String) properties.get("album"));
        sb.duration((Long) properties.get("duration"));
        track = (String) properties.get("mp3.id3tag.track");
        disc = (String) properties.get("mp3.id3tag.disc");
        if (track != null) {
            String[] trackInfos = track.split("/");
            sb.trackNumber(Integer.valueOf(trackInfos[0]));
        }
        if (disc != null) {
            String[] discInfos = disc.split("/");
            sb.discNumber(Integer.valueOf(discInfos[0]));
        }
        sb.path(path.getFileName().toString());
        return sb.build();
    }

    public Song findById(long songId) {
        for (Map.Entry<Long, Song> entry : repository.entrySet()) {
            if (entry.getKey() == songId) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void save(Song song) {
        synchronized (lock) {
            if (song.getId() == null) {
                song.setId(generator.incrementAndGet());
            }
            repository.put(song.getId(), song);
        }
    }

    public void truncate() {
        synchronized (lock) {
            generator.set(0);
            repository.clear();
        }
    }

    public List<Song> findAll() {
        return new ArrayList<>(repository.values());
    }
}
