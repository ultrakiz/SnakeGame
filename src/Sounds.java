/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sound.sampled.*;
import java.io.File;

/**
 *
 * @author user
 */
public class Sounds {
    static class PlayMus implements Runnable {
        File f1 = new File("res/contra.wav");
        AudioInputStream stream;
        AudioFormat format;
        DataLine.Info info;
        Clip clip;
    
        public void run(){
            try {

                stream = AudioSystem.getAudioInputStream(f1);
                format = stream.getFormat();
                info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                
                clip.start();
            }
            catch (Exception e) {
            };
        }
    }
    
    static class PlaySnd implements Runnable {
        File f = new File("res/sound.wav");
        AudioInputStream stream;
        AudioFormat format;
        DataLine.Info info;
        Clip clip;
    
        public void run(){
            try {

                stream = AudioSystem.getAudioInputStream(f);
                format = stream.getFormat();
                info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                
                clip.start();
            }
            catch (Exception e) {
            };
        }
    }
}
