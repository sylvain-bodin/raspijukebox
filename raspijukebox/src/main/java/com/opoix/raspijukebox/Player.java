package com.opoix.raspijukebox;

import com.opoix.raspijukebox.config.ConfigurationManager;
import com.opoix.raspijukebox.entity.Song;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by copoix on 1/2/16.
 */
public class Player {
    private static Player instance = new Player();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ConfigurationManager config = ConfigurationManager.getInstance();

    public static Player getInstance() {
        return instance;
    }

    public AdvancedPlayer currentPlayer;

    public void play(Song song) {
        play(song, true);
    }

    public void stop() {
        if (currentPlayer == null) {
            return;
        }
        currentPlayer.close();
    }

    private void playSong(Song song) {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(new File(config.getProperty("song.path"), song.getPath()));
            currentPlayer = new AdvancedPlayer(fis);
            currentPlayer.play();
            currentPlayer.close();
        } catch (JavaLayerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void playAll(List<Song> songList, boolean repeat) {
        if (currentPlayer != null) {
            executorService.shutdownNow();
            currentPlayer.close();
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(() -> {
            do {
                songList.forEach(song -> playSong(song));
            } while (repeat);
        });
    }

    public void play(Song song, boolean stopCurrent) {
        if (stopCurrent && currentPlayer != null) {
            executorService.shutdownNow();
            currentPlayer.close();
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(() -> {
            playSong(song);
        });
    }

}
