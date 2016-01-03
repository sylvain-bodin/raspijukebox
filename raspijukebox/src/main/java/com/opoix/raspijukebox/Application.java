package com.opoix.raspijukebox;

import com.opoix.raspijukebox.config.ConfigurationManager;
import com.opoix.raspijukebox.repository.SongRepository;
import com.opoix.raspijukebox.resource.SongResource;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by copoix on 1/2/16.
 */
public class Application {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:5555/";

    private static SongRepository songRepository = SongRepository.getInstance();


    public static HttpServer startServer() throws IOException  {
        HttpServer server = HttpServer.create(new InetSocketAddress(5555), 0);
        SongResource songRes = SongResource.getInstance();
        server.createContext("/play", exchange -> songRes.play(exchange));
        server.createContext("/scan", exchange -> songRes.scan(exchange));
        server.createContext("/list", exchange -> songRes.htmlList(exchange));
        server.createContext("/stop", exchange -> songRes.stop(exchange));

        //server.createContext("/get", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        return server;
    }

    public static void main(String[] args) throws IOException {
        SoundSystem.showInfo();
        ConfigurationManager config = ConfigurationManager.getInstance();
        config.load();
        System.out.print(String.format("Scanning..."));
        songRepository.scan();
        System.out.println(String.format("OK"));
        System.out.print(String.format("Starting server..."));
        final HttpServer server = startServer();
        System.out.println(String.format("OK"));
        System.in.read();
        server.stop(0);
    }
}
