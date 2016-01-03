package com.opoix.raspijukebox;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 * Created by copoix on 1/3/16.
 */
public class SoundSystem {
    public static void showInfo() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            System.out.println("Found Mixer: " + mixerInfo);
            Mixer m = AudioSystem.getMixer(mixerInfo);

            Line.Info[] sourceLines = m.getSourceLineInfo();
            for (Line.Info li : sourceLines) {
                System.out.println("    Found source line: " + li);
                try {
                    m.open();
                } catch (LineUnavailableException e) {
                    System.out.println("        Source Line unavailable.");
                }
            }

            Line.Info[] targetLines = m.getTargetLineInfo();
            for (Line.Info li : targetLines) {
                System.out.println("    Found target line: " + li);
                try {
                    m.open();
                } catch (LineUnavailableException e) {
                    System.out.println("        Target Line unavailable.");
                }
            }
        }
    }
}
