package com.opoix.raspijukebox.resource;

import com.opoix.raspijukebox.Player;
import com.opoix.raspijukebox.config.ConfigurationManager;
import com.opoix.raspijukebox.entity.Song;
import com.opoix.raspijukebox.repository.SongRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SongResource {

    private static SongResource instance = new SongResource();

    private static SongRepository songRepository = SongRepository.getInstance();
    private static ResourceUtils resUtils = ResourceUtils.getInstance();

    private SongResource() {
    }

    public static SongResource getInstance() {
        return instance;
    }

    public void scan(HttpExchange exchange) throws IOException {
        songRepository.scan();
        String response = "Scan complete.";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public List<Song> list() throws IOException {
        return songRepository.findAll();
    }

    public void play(HttpExchange exchange) throws IOException {
        HttpParams params = resUtils.getParams(exchange);
        Song song = songRepository.findById(params.getLong("songId"));
        try {
            Boolean repeat = params.getBoolean("repeat", true);
            Player.getInstance().playAll(Collections.singletonList(song), repeat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = "Playing " + song.getArtist() + " - " + song.getTitle() + "...";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void playAll(HttpExchange httpExchange) {
        HttpParams params = resUtils.getParams(httpExchange);
        Boolean repeat = params.getBoolean("repeat", true);
        Random rand = new Random();
        Player.getInstance().playAll(songRepository.findAll().stream().sorted((song1, song2) -> rand.nextInt()).collect(Collectors.toList()), repeat);
    }

    public void htmlList(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        Writer writer = new BufferedWriter(new OutputStreamWriter(os));

        songRepository.findAll().forEach(song -> {
            try {
                String line = MessageFormat.format("<a href=\"play?songId={0}\">{1} - {2}</a><br />\n", song.getId(), song.getArtist(), song.getTitle());
                writer.write(line);
            } catch (IOException e) {
                return;
            }
        });
        writer.flush();
        os.close();
    }

    public void stop(HttpExchange exchange) throws IOException {
        Player.getInstance().stop();
        String response = "Stopped.";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}