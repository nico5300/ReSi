package me.ocin5300.ReSi.gui.hauptfenster;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import me.ocin5300.ReSi.Datei;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;
import org.fxmisc.undo.UndoManager;
import org.fxmisc.undo.UndoManagerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HauptfensterController implements Initializable{
    private CodeArea codeArea;
    private VirtualizedScrollPane<CodeArea> leftScrollPane;
    private Datei datei;
    private FileChooser fileChooser;
    private Pattern languagePattern;

    @FXML
    private AnchorPane leftSide;

    @FXML
    private AnchorPane rightSide;

    @FXML
    private MenuItem menuEditorRueckgaengigButton;

    @FXML
    private MenuItem menuEditorWiederherstellenButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        leftScrollPane = new VirtualizedScrollPane<>(codeArea);
        codeArea.requestFollowCaret();

        codeArea.setOnKeyPressed(this::onKeyPressedCodeArea);

        leftSide.getChildren().addAll(leftScrollPane);
        leftSide.setBottomAnchor(leftScrollPane, 0d);
        leftSide.setLeftAnchor(leftScrollPane, 0d);
        leftSide.setRightAnchor(leftScrollPane, 0d);
        leftSide.setTopAnchor(leftScrollPane, 0d);

        datei = new Datei();
        fileChooser = new FileChooser();

        menuEditorWiederherstellenButton.disableProperty().bind(codeArea.redoAvailableProperty().map(x -> !x));
        menuEditorRueckgaengigButton.disableProperty().bind(codeArea.undoAvailableProperty().map(x -> !x));


    }


    @FXML
    public void onMenuDateiNeuClick(ActionEvent e) {
        if(datei.isDifferent(codeArea.getText())) {
            Alert al = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL, ButtonType.NO);
            al.setTitle("Speichern?");
            al.setHeaderText("Vor dem Schließen speichern?");
            al.setContentText("Möchten Sie vor dem Schließen speichern?\nNicht gespeicherte Änderungen gehen verloren.");
            Optional<ButtonType> buttonTypeOptional = al.showAndWait();

            if(buttonTypeOptional.isPresent()) {
                ButtonType buttonType = buttonTypeOptional.get();

                if(buttonType == ButtonType.YES) {
                    if(dateiSpeichern()) {
                        if(!datei.isDifferent(codeArea.getText())) {
                            datei = new Datei();
                            codeArea.replaceText("");
                        }
                    }
                }
                if(buttonType == ButtonType.NO) {
                    datei = new Datei();
                    codeArea.replaceText("");
                }
            }
        }
    }

    public void onMenuDateiSchliessenClick(ActionEvent actionEvent) {
        onClose();
    }

    public void onClose() {
        if(datei.isDifferent(codeArea.getText())) {
            Alert al = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL, ButtonType.NO);
            al.setTitle("Speichern?");
            al.setHeaderText("Vor dem Schließen speichern?");
            al.setContentText("Möchten Sie vor dem Schließen speichern?\nNicht gespeicherte Änderungen gehen verloren.");
            Optional<ButtonType> buttonTypeOptional = al.showAndWait();

            if (buttonTypeOptional.isPresent()) {
                ButtonType buttonType = buttonTypeOptional.get();

                if(buttonType == ButtonType.YES) {
                    if(dateiSpeichern())
                        if(!datei.isDifferent(codeArea.getText()))
                            Platform.exit();
                }
                if(buttonType == ButtonType.NO) {
                    Platform.exit();
                }

            }
        }
    }


    /////////////////////// SPEICHERN UNTER ///////////////////////////////////


    public void onMenuDateiSpeichernUnterClick(ActionEvent actionEvent) {
        dateiSpeichernUnter();
    }

    private boolean dateiSpeichernUnter() {
        fileChooser.setTitle("Assemblerdatei speichern");
        File file = fileChooser.showSaveDialog(codeArea.getScene().getWindow());

        if(file == null) {
            return false;
        }


        String text2save = codeArea.getText();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                datei = new Datei(file.getAbsolutePath());
                datei.saveNewContent(text2save);
                return null;
            }
        };

        task.setOnFailed(workerStateEvent -> {
            Alert al2 = new Alert(Alert.AlertType.ERROR);
            al2.setTitle("Fehler");
            al2.setHeaderText("Fehler beim Speichern");
            al2.setContentText("Die Datei " + datei.getFile().getName() + "\nkonnte nicht gespeichert werden.");
            al2.showAndWait();
        });

        Thread t = new Thread(task);
        t.setDaemon(false);
        t.start();
        return false;
    }



    /////////////////////// DATEI SPEICHERN ////////////////////////////////////

    public void onMenuDateiSpeichernClick(ActionEvent actionEvent) {
        dateiSpeichern();
    }

    public boolean dateiSpeichern() {

        if(datei.isInitial()) {
            return dateiSpeichernUnter();
        }

        String text2save = codeArea.getText();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    datei.saveNewContent(text2save);
                    return null;
                }
            };

            task.setOnFailed(workerStateEvent -> {
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Fehler");
                al.setHeaderText("Fehler beim Speichern");
                al.setContentText("Die Datei " + datei.getFile().getName() + "\nkonnte nicht gespeichert werden.");
                al.showAndWait();
            });

            Thread t = new Thread(task);
            t.setDaemon(false);
            t.start();

            return true;

    }


    //////////////////// DATEI ÖFFNEN ///////////////////////////////////////

    public void onMenuDateiOeffnenClick(ActionEvent actionEvent) {
        if(datei.isDifferent(codeArea.getText())) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Speichern?");
            alert.setHeaderText("Wollen Sie vorher speichern?");
            alert.setContentText("Wenn Sie nicht speichern, geht Ihr\nbisheriger Code verloren.");

            Optional<ButtonType> buttonTypeOptional = alert.showAndWait();
            if(buttonTypeOptional.isPresent()) {

                ButtonType buttonType = buttonTypeOptional.get();

                if(buttonType == ButtonType.YES) {
                    if (datei.isInitial()) {
                        dateiSpeichernUnter();
                    } else {
                        dateiSpeichern();

                    }
                }

            }
        }

        fileChooser.setTitle("Assemblerdatei Öffnen");
        File file = fileChooser.showOpenDialog(codeArea.getScene().getWindow());


            if(file != null) {

                fileChooser.setInitialDirectory(file.getParentFile());

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws IOException {

                        datei = new Datei(file.getAbsolutePath());
                        return null;

                    }
                };

                task.setOnSucceeded(e -> codeArea.replaceText(datei.getContent()));


                task.setOnFailed(workerStateEvent -> {
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Fehler");
                    al.setHeaderText("Lesefehler");
                    al.setContentText("Die Datei " + file.getName() + "\nkonnte nicht geöffnet werden. Existiert die Datei?");
                    al.showAndWait();
                    workerStateEvent.getSource().getException().printStackTrace();
                });

                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();


            }


    }



    public void onMenuEditorWiederherstellenClick(ActionEvent e) {
        codeArea.redo();
    }

    public void onMenuEditorRueckgaengigClick(ActionEvent e) {
        codeArea.undo();
    }

    public void onKeyPressedCodeArea(KeyEvent e) {

        boolean newLineAdded;
        if(e.getCode() == KeyCode.ENTER){
            newLineAdded = true;
            System.out.println("ENTER");
        }
        else if(e.getCode() == KeyCode.BACK_SPACE) {
            newLineAdded = false;
            System.out.println("BSP");
        } else {
            return;
        }

        int lines = codeArea.getParagraphs().size()-1;
        int caretLine = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor();


        for (int i=0; i<lines; i++) {
            if(i == caretLine)
                continue;

            String x = codeArea.getParagraph(i).getText().toUpperCase();
            if(x.startsWith("JUMP") || x.startsWith("JEQ") || x.startsWith("JGT") || x.startsWith("JGE") ||
                    x.startsWith("JLT") || x.startsWith("JLE")) {

                Scanner sc = new Scanner(x);
                sc.next();
                if(sc.hasNextInt()) {
                    int jump2line = sc.nextInt();
                    int updatedJump2line = jump2line;
                    if(jump2line < 0 || jump2line > codeArea.getParagraphs().stream().count())  // if u would jump out of bounds...
                        continue;

                    if(newLineAdded) {
                        if(jump2line > caretLine) {
                            updatedJump2line = jump2line + 1;
                        }


                    } else {
                        if(jump2line > caretLine) {
                            updatedJump2line = jump2line - 1;
                        }

                    }

                    int replaceIndex = codeArea.getParagraph(i).getText().indexOf(Integer.toString(jump2line));
                    System.out.println(x);
                    System.out.println("uJ2L: " + updatedJump2line);
                    System.out.println("R.Ind: " + replaceIndex);
                    System.out.println("i:" + i);
                    System.out.println("length of j2l: "+ (Integer.toString(jump2line).length()-1));


                    System.out.println("\n\n\n");

                    int caretIndexPosition = codeArea.getCaretPosition();       // TODO: FEHLER FINDEN!!!!!!!!!

                    codeArea.replaceText(i+1, replaceIndex-1, i+1, replaceIndex + Integer.toString(jump2line).length(), Integer.toString(updatedJump2line));
                    codeArea.moveTo(caretIndexPosition);

                }

            }

        }

        List<String> jumpList =
        codeArea.getParagraphs().stream().map(x -> x.getText())
                .map(x -> x.toUpperCase())
                .filter(x -> {
                    return x.contains("JUMP") || x.contains("JEQ") || x.contains("JGT") || x.contains("JGE") ||
                            x.contains("JLT") || x.contains("JLE");
                })
                .map(x -> {
                    for(int i=0; i<x.length()-2; i++) {
                        if(x.substring(i, i+2).equals("--")) {
                            if(i == 0)
                                return null;
                            return x.substring(0, i-1);
                        }
                    }
                    return x;
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
        jumpList.stream();

    }
}
