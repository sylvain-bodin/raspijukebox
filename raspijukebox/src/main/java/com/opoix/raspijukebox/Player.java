package com.opoix.raspijukebox;

import com.opoix.raspijukebox.config.ConfigurationManager;
import com.opoix.raspijukebox.entity.Song;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by copoix on 1/2/16.
 */
public class Player {


    private static Player instance = new Player();
    private ConfigurationManager config = ConfigurationManager.getInstance();

    public static Player getInstance() {
        return instance;
    }

    private AdvancedPlayer currentPlayer;
    private ReentrantLock playerLock = new ReentrantLock();
    private Thread currentThread;
    private List<Song> playList = new ArrayList<>();
    private int playListIndex = 0;

    @Getter
    @Setter
    private boolean repeat = false;

    @Getter
    @Setter
    private boolean random = false;

    @Getter
    private boolean playing = false;


    private void play() {
        playerLock.lock();
        try {
            if (currentThread != null && currentThread.isAlive()) {
                currentThread.interrupt();
            }
            currentThread = new Thread(() -> {
                try {
                    Random rand = new Random();
                    playing = true;
                    while (playing) {
                        if (playListIndex >= playList.size()) {
                            break;
                        }
                        Song song = playList.get(playListIndex);
                        playSong(song);

                        if (repeat && playListIndex >= playList.size()) {
                            playListIndex = 0;
                        }

                        if (random) {
                            playListIndex = rand.nextInt(playList.size());
                        } else {
                            playListIndex++;
                        }
                    }
                } finally {
                    playing = false;
                }
            });
            currentThread.start();
        } finally {
            playerLock.unlock();
        }
    }

    public void stop() {
        playerLock.lock();
        playing = false;
        try {
            if (currentPlayer != null) {
                currentPlayer.close();
                currentPlayer = null;
            }
            if (currentThread != null && currentThread.isAlive()) {
                currentThread.interrupt();
                currentThread = null;
            }
        } finally {
            playerLock.unlock();
        }
    }

    public void clear() {
        playerLock.lock();
        try {
            playList.clear();
            playListIndex = 0;
        } finally {
            playerLock.unlock();
        }
    }

    private void playSong(Song song) {
        try {
            stop();
            FileInputStream fis = new FileInputStream(new File(config.getProperty("song.path"), song.getPath()));
            currentPlayer = new AdvancedPlayer(fis);
            playing = true;
            try {
                currentPlayer.play();
            } finally {
                playing = false;
            }
        } catch (JavaLayerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void playAll(List<Song> songList) {
        playerLock.lock();
        try {
            stop();
            clear();
            playList.addAll(songList);
            play();
        } finally {
            playerLock.unlock();
        }
    }

    public void play(Song song) {
        playAll(Arrays.asList(song));
    }

}
