package com.opoix.raspijukebox.resource;

import com.google.gson.Gson;
import com.opoix.raspijukebox.Player;
import com.opoix.raspijukebox.entity.Song;
import com.opoix.raspijukebox.repository.SongRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
        allowCROS(exchange);
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
            Player.getInstance().playAll(Collections.singletonList(song));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = "Playing " + song.getArtist() + " - " + song.getTitle() + "...";
        allowCROS(exchange);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void playAll(HttpExchange exchange) throws IOException {
        HttpParams params = resUtils.getParams(exchange);
        Boolean repeat = params.getBoolean("repeat", true);
        Random rand = new Random();
        List<Song> songList = songRepository.findAll().stream().sorted((song1, song2) -> rand.nextInt()).collect(Collectors.toList());
        Player.getInstance().playAll(songList);
        StringBuilder responseSb = new StringBuilder();
        songList.forEach((song) -> {
            responseSb.append(String.format("%s - %s%n", song.getArtist(), song.getTitle()));
        });
        String response = responseSb.toString();
        allowCROS(exchange);
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        Writer writer = new PrintWriter(os);
        writer.write(response);
        writer.close();
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

    /**
     * Return the list of the songs in a JSON format
     * @param exchange the HttpExchange form the service
     * @throws IOException
     */
    public void jsonList(HttpExchange exchange) throws IOException {
        allowCROS(exchange);
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        Writer writer = new BufferedWriter(new OutputStreamWriter(os));
        Gson gson = new Gson();
        writer.write(gson.toJson(songRepository.findAll()));
        writer.flush();
        os.close();
    }

    public void stop(HttpExchange exchange) throws IOException {
        Player.getInstance().stop();
        String response = "Stopped.";
        allowCROS(exchange);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * Modify the header to allow services CrossDomain on GET.
     * @param exchange the HttpExchange form the service.
     */
    private void allowCROS(HttpExchange exchange){
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods"," GET");
        exchange.getResponseHeaders().add("Access-Control-Max-Age","151200");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers","x-requested-with,Content-Type");
    }
}