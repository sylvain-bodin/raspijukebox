/**
 * Created by copoix on 1/2/16.
 */
package com.opoix.raspijukebox.entity;

import lombok.Data;
import lombok.experimental.Builder;

@Data
@Builder
public class Song {
    private Long id;
    private String artist;
    private String title;
    private String album;
    private Integer trackNumber;
    private Integer discNumber;
    private Long duration;
    private String path;
}
