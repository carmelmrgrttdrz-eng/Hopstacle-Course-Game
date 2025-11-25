import javax.sound.sampled.*;
import java.io.File;

public class MusicManager {
    private static Clip clip;
    private static boolean muted = false; 

    public static void playMusic(String path) { //play music
        try {
            //if music is already loaded, stop it before changing
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            File musicFile = new File(path);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);

            clip = AudioSystem.getClip();
            clip.open(audioInput);

            if(path.equals("music/mainMusic.wav"))
                clip.loop(Clip.LOOP_CONTINUOUSLY);

            if (!muted) {
                clip.start();
            }

        } catch (Exception e) {
            System.out.println("Music Error: " + e);
        }
    }

    public static void toggleMute() { //for mute
        if (clip == null) return;

        muted = !muted; //invert the value

        if (muted) 
            clip.stop();
        else 
            clip.start();
    }

    public static boolean isMuted() { //get boolean if its musted or not
        return muted;
    }
}
