package me.ocin5300.ReSi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.ocin5300.ReSi.gui.hauptfenster.Hauptfenster;
import me.ocin5300.ReSi.gui.hauptfenster.HauptfensterController;

public class Main extends Application {



    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Hauptfenster hf = new Hauptfenster();
        Scene sc = new Scene(hf.getView());
        stage.setScene(sc);
        stage.setTitle("ReSi - RegistermaschinenSimulation");
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            ((HauptfensterController) hf.getPresenter()).onClose();
        });
    }

    @Override
    public void stop() {

    }
}
