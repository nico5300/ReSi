package me.ocin5300.ReSi;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.ocin5300.ReSi.gui.hauptfenster.Hauptfenster;
import me.ocin5300.ReSi.gui.hauptfenster.HauptfensterController;

import java.util.HashMap;

public class Main extends Application {



    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("injectionMap" , hm);
        Injector.setConfigurationSource(hm::get);
        Hauptfenster hf = new Hauptfenster();
        Scene sc = new Scene(hf.getView());

        stage.setScene(sc);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/me/ocin5300/ReSi/gui/Binary-Code-16.png")));
        stage.setTitle("ReSi - RegistermaschinenSimulation");
        stage.show();
        stage.setOnCloseRequest(windowEvent ->
                ((HauptfensterController) hf.getPresenter()).onClose(windowEvent));
    }

    @Override
    public void stop() {

    }
}
