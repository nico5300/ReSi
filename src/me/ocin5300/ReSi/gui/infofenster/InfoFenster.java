package me.ocin5300.ReSi.gui.infofenster;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class InfoFenster {

    private Dialog<ButtonType> dialog;

    public InfoFenster() {
        dialog = new Dialog<>();
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("/me/ocin5300/ReSi/gui/Binary-Code-16.png")));
        ImageView img = new ImageView(new Image(this.getClass().getResourceAsStream("/me/ocin5300/ReSi/gui/Binary-Code-128.png")));
        img.setFitHeight(60);
        img.setFitWidth(60);
        dialog.setGraphic(img);
        dialog.setTitle("Info...");
        dialog.setHeaderText("Informationen zu ReSi");
        dialog.setContentText("ReSi - Die RegistermaschinenSimulation\n\n" +
                              "Autor: Nico Greger 2018, <nico_5300@yahoo.de>\n\n" +
                              "Lizenzen: Siehe unten");

        String license = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(
                "/me/ocin5300/ReSi/gui/infofenster/lizenz.txt"))).lines().collect(Collectors.joining("\n"));
        TextArea textArea = new TextArea(license);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        dialog.getDialogPane().setPrefWidth(560);
        dialog.getDialogPane().setPrefHeight(400);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.getDialogPane().setExpandableContent(textArea);

        dialog.initModality(Modality.NONE);

        dialog.show();

    }
}
