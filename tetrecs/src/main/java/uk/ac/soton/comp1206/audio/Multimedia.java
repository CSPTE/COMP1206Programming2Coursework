package uk.ac.soton.comp1206.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.time.Duration;

/**
 * Class is responsible for playing audio
 */
public class Multimedia {
    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;

    /**
     * Used to play sound effects
     * @param file File to play
     */
    public void playAudio(String file) {
        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to play background music
     * @param file File to play
     */
    public void playMusic(String file) {
        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        try {
            Media play = new Media(toPlay);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.setCycleCount(Integer.MAX_VALUE);
            musicPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter method for the musicPlayer
     * @return musicPlayer
     */
    public MediaPlayer getMusicPlayer(){
        return musicPlayer;
    }
}
