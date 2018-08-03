
#### Audio Format
16000kHz, Mono, PCM 16bit

```java
static AudioTrack newAudioTrack() {
    int intSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    return new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
}
```
