package com.opoix.raspijukebox.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Properties;

/**
 * Created by copoix on 1/2/16.
 */
public class ConfigurationManager {
    private static ConfigurationManager instance = new ConfigurationManager();
    private Properties props = new Properties();


    public static ConfigurationManager getInstance() {
        return instance;
    }

    public void load() throws IOException {
        String home = System.getProperty("user.home");
        File file = new File(home + "/.raspijukebox", "config.properties");
        if (file.exists()) {
            try {
                props.load(new FileInputStream(file));
                return;
            } catch (IOException e) {
                // an error occured
            }
        }
        props.load(this.getClass().getResourceAsStream("/config.properties"));
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

}
