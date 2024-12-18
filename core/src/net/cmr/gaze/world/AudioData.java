package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;

public class AudioData {

    private String audio;
    private float audioVolume;
    private float audioPitch;
    private float audioPanX, audioPanY;

    public AudioData(String audio, float audioVolume, float audioPitch, float audioPanX, float audioPanY) {
        this.audio = audio;
        this.audioVolume = audioVolume;
        this.audioPitch = audioPitch;
        this.audioPanX = audioPanX;
        this.audioPanY = audioPanY;
    }
    public AudioData(String audio, float audioVolume, float audioPitch) {
        this(audio, audioVolume, audioPitch, 0, 0);
    }

    public String getAudio() {
        return audio;
    }
    public float getVolume() {
        return audioVolume;
    }
    public float getPitch() {
        return audioPitch;
    }
    public float getPanX() {
        return audioPanX;
    }
    public float getPanY() {
        return audioPanY;
    }

    public void playSound(Gaze game) {
        game.playSound(audio, getVolume(), getPitch());
    }

    public void write(DataBuffer buffer) throws IOException {
        buffer.writeUTF(audio);
        buffer.writeFloat(getVolume());
        buffer.writeFloat(getPitch());
        buffer.writeFloat(getPanX());
        buffer.writeFloat(getPanY());
    }
    public static AudioData read(DataInputStream inputStream, int bufferSize) throws IOException {
        AudioData data = new AudioData(inputStream.readUTF(), inputStream.readFloat(), inputStream.readFloat(), inputStream.readFloat(), inputStream.readFloat());
        return data;
    }

}