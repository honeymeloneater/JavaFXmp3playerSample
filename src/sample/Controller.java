package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;

public class Controller {
    @FXML
    public Button playButton;
    public Button pauseButton;
    public Slider timeSlider;
    public MediaView mediaView;
    public AnchorPane anchPane1;
    public Label playTime;
    public Button volumeButton;
    public Slider volumeSlider;
    public Button changeButton;

    @FXML
    public void initialize() {
        Model.initMedia(timeSlider,mediaView,playTime,volumeSlider,anchPane1);
    }
    @FXML
    public void OnActionButtonHandler(ActionEvent event){
        Button button = (Button)event.getSource();
        if(button==playButton)
        {
            Model.playFunc(mediaView);
        }
        if(button==pauseButton)
        {
            Model.pauseFunc(mediaView);
        }
        if(button==volumeButton){
            Model.volumeChanging(volumeSlider);
        }
        if(button==changeButton){
            Model.changeSong(mediaView,playTime,timeSlider);
        }
    }
}
