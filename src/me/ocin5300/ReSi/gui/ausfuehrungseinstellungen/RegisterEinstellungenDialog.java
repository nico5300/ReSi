package me.ocin5300.ReSi.gui.ausfuehrungseinstellungen;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.ocin5300.ReSi.logik.NumberConverter;

public class RegisterEinstellungenDialog {


    private SimpleIntegerProperty tick;
    private TextInputDialog dialog;

    public RegisterEinstellungenDialog(SimpleIntegerProperty tick) {

        this.tick = tick;
        dialog = new TextInputDialog(tick.getValue().toString());
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("/me/ocin5300/ReSi/gui/Binary-Code-16.png")));

        ImageView img = new ImageView(new Image(this.getClass().getResourceAsStream("/me/ocin5300/ReSi/gui/Settings-01.png")));
        Reflection reflection = new Reflection(-10, 0.3, 0.75, 0);
        img.setEffect(reflection);

        dialog.setGraphic(img);
        dialog.setContentText("Ausführungsgeschwindigkeit in Millisekunden:");
        dialog.setHeaderText("Ausführungseinstellungen");
        dialog.setTitle("Einstellungen");

        dialog.getEditor().textProperty().bindBidirectional(tick, new NumberConverter(tick, dialog.getEditor()));
    }

    public void dialogAnzeigen() {
        if(!dialog.showAndWait().isPresent()) {
            tick.set(Integer.parseInt(dialog.getDefaultValue()));
        }
    }

}
