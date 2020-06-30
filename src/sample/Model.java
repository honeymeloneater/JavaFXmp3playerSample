package sample;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.*;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class Model {

    public static void initMedia(Slider timeSlider, MediaView mediaView, Label playTime, Slider volumeSlider, AnchorPane anchPane1){
        File f = new FileChooser().showOpenDialog(null);
        Media media = new Media(f.toURI().toString());
        //If you wanna take metadata from media (loading asynchronously)
       /* media.getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {
                if (ch.wasAdded()) {
                    //System.out.println(ch.getKey());
                    //System.out.println(ch.getValueAdded());
                    if(ch.getKey().equals("image")){
                    }
                    //System.out.println(mediaView.getMediaPlayer().getStatus());
                }
            }
        });*/
        volumeSlider.setMax(100);
        volumeSlider.setValue(100);
        MediaPlayer mediaPlayer= new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);

        //Start of handler's block
        Line[] lines= new Line[128];
        for(int i=0;i<128;i++){
            Line line = new Line(150+i*3, 250, 150+ i*3, 250);
            lines[i]=line;
            anchPane1.getChildren().add(lines[i]);
        }
        mediaPlayer.totalDurationProperty().addListener((obs, oldDuration, newDuration) -> timeSlider.setMax(newDuration.toSeconds()));

        mediaView.mediaPlayerProperty().addListener(e->{
            mediaView.getMediaPlayer().setAudioSpectrumListener(new AudioSpectrumListener() {
                @Override
                public void spectrumDataUpdate(double v, double v1, float[] floats, float[] floats1) {
                    for(int i=0;i<Math.max(128,floats.length);i++)
                        lines[i].setEndY(250 - floats[i] + mediaView.getMediaPlayer().getAudioSpectrumThreshold());
                }
            });
        });

        volumeSlider.valueProperty().addListener(e->{
            if(volumeSlider.isPressed())
                mediaView.getMediaPlayer().setVolume(volumeSlider.getValue()/100);
        });

        mediaPlayer.getMedia().durationProperty().addListener(e-> {
        Duration duration = mediaPlayer.getMedia().getDuration();
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) ->
        {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(mediaPlayer.currentTimeProperty().get().toSeconds());
                Duration currentTime = mediaPlayer.getCurrentTime();
                playTime.setText(Model.formatTime(currentTime, duration));
            }
        });
        });

        timeSlider.setOnMousePressed(c-> {
            mediaView.getMediaPlayer().seek(Duration.seconds(timeSlider.getValue()));
        });

        timeSlider.setOnMouseDragged(c-> mediaView.getMediaPlayer().seek(Duration.seconds(timeSlider.getValue())) );

        timeSlider.valueProperty().addListener(d -> {
            if (timeSlider.isValueChanging()) {
                mediaView.getMediaPlayer().seek(Duration.seconds(timeSlider.getValue()));
            }
        });
        //End of Handler's Block

        mediaView.setMediaPlayer(mediaPlayer);
    }

    public static void volumeChanging(Slider volumeSlider){
        if(!volumeSlider.isVisible())
            volumeSlider.setVisible(true);
        else
            volumeSlider.setVisible(false);
    }

    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    public static void playFunc(MediaView mediaView){
        mediaView.getMediaPlayer().play();
    }

    public static void pauseFunc (MediaView mediaView){
        mediaView.getMediaPlayer().pause();
    }

    public static void changeSong (MediaView mediaView, Label playTime, Slider timeSlider){
        File f= new FileChooser().showOpenDialog(mediaView.getScene().getWindow());
        Media media = new Media(f.toURI().toString());
        mediaView.getMediaPlayer().stop();
        MediaPlayer mediaPlayer= new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.totalDurationProperty()
                .addListener((obs, oldDuration, newDuration) -> timeSlider.setMax(newDuration.toSeconds()));
        mediaPlayer.getMedia().durationProperty().addListener(e-> {
            Duration duration = mediaPlayer.getMedia().getDuration();
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) ->
            {
                if (!timeSlider.isValueChanging()) {
                    timeSlider.setValue(mediaPlayer.currentTimeProperty().get().toSeconds());
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(Model.formatTime(currentTime, duration));
                }
            });
        });
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }
}
