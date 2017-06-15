package ru.hh.findjob;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by lollipop on 13.06.2017.
 */
public class MainController implements Initializable {
    @FXML
    private TextField vacancyName;
    @FXML
    private TextField tokenField;
    @FXML
    private TextField resumeIdField;
    @FXML
    private ComboBox comboBox;
    @FXML
    private TextArea areaMessage;
    @FXML
    private ListView<String> vacancyView;
    @FXML
    private Button startButton;
    private Model model;

    private MainApp mainApp;

    public void setMainApp(MainApp app) {
        mainApp = app;
    }

    public TextField getTokenField() {
        return tokenField;
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public TextArea getAreaMessage() {
        return areaMessage;
    }

    public ListView<String> getVacancyView() {
        return vacancyView;
    }

    public Button getStartButton() {

        return startButton;
    }

    @FXML
    public void onClickStartButton() {
        if (vacancyName.getText().isEmpty() ||
                tokenField.getText().isEmpty() ||
                resumeIdField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty fields");
            alert.setHeaderText(null);
            alert.setContentText("Some of the fields are empty");
            alert.show();
            return;
        }
        Model model = new Model(this);
        vacancyView.getItems().clear();
        startButton.setDisable(true);
        model.setValuables(tokenField.getText(), (String) comboBox.getSelectionModel().getSelectedItem(), areaMessage.getText(),
                resumeIdField.getText(), vacancyName.getText());

        new Thread(model).start();
    }

    public void addListViewItem(String str) {
        vacancyView.getItems().add(str);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model(this);
        initializeCombobox();
        initializeVacancyViewListener();
    }

    private void initializeCombobox() {
        comboBox.getItems().removeAll(comboBox.getItems());
        comboBox.setValue("Does not matter");
        comboBox.getItems().addAll("Does not matter", "No experience",
                "1-3 years", "3-6 years", "More than 6 years");
    }

    private void initializeVacancyViewListener() {
        vacancyView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String url = vacancyView.getSelectionModel().getSelectedItem().split("[\n]")[1].split(" ")[1];

                mainApp.getHostServices().showDocument(url);

            }
        });

    }
}
