package me.ocin5300.ReSi.gui.hauptfenster;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import me.ocin5300.ReSi.Datei;
import me.ocin5300.ReSi.logik.NumberConverter;
import me.ocin5300.ReSi.logik.Registermaschine;
import me.ocin5300.ReSi.logik.RegistermaschinenListener;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HauptfensterController implements Initializable, RegistermaschinenListener{
    private CodeArea codeArea;
    private VirtualizedScrollPane<CodeArea> leftScrollPane;
    private Datei datei;
    private FileChooser fileChooser;
    private Registermaschine registermaschine;

    @FXML
    private AnchorPane leftSide;

    @FXML
    private AnchorPane rightSide;

    @FXML
    private MenuItem menuEditorRueckgaengigButton;

    @FXML
    private MenuItem menuEditorWiederherstellenButton;

    @FXML
    private MenuItem menuProgrammStartenButton;

    @FXML
    private MenuItem menuStepButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button rightSideStartButton;

    @FXML
    private Button rightSideStepButton;

    @FXML
    private CheckBox rightSideAutoRadioButton;

    @FXML
    private TextField rsInstructionTextField;

    @FXML
    private TextField rsAkkumulatorTextField;

    @FXML
    private RadioMenuItem menuAutomatischAusfuehren;


        @FXML
        private TextField rsReg1;

        @FXML
        private TextField rsReg2;

        @FXML
        private TextField rsReg3;

        @FXML
        private TextField rsReg4;

        @FXML
        private TextField rsReg5;

        @FXML
        private TextField rsReg6;

        @FXML
        private TextField rsReg7;

        @FXML
        private TextField rsReg8;

        @FXML
        private TextField rsReg9;

        @FXML
        private TextField rsReg10;

        @FXML
        private TextField rsReg11;

        @FXML
        private TextField rsReg12;

        @FXML
        private TextField rsReg13;

        @FXML
        private TextField rsReg14;

        @FXML
        private TextField rsReg15;

        @FXML
        private TextField rsReg16;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // CodeArea einrichten & hinzufügen
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        leftScrollPane = new VirtualizedScrollPane<>(codeArea);
        codeArea.requestFollowCaret();

        // CodeArea in Scene einbauen
        leftSide.getChildren().addAll(leftScrollPane);
        AnchorPane.setBottomAnchor(leftScrollPane, 0d);
        AnchorPane.setLeftAnchor(leftScrollPane, 0d);
        AnchorPane.setRightAnchor(leftScrollPane, 0d);
        AnchorPane.setTopAnchor(leftScrollPane, 0d);

        Tooltip tt = new Tooltip();
        tt.textProperty().bind(statusLabel.textProperty());
        statusLabel.setTooltip(tt);

        // Datenmodel erstellen
        datei = new Datei();
        fileChooser = new FileChooser();
        newRegistermaschine();

        // Bindings
        menuEditorWiederherstellenButton.disableProperty().bind(codeArea.redoAvailableProperty().map(x -> !x));
        menuEditorRueckgaengigButton.disableProperty().bind(codeArea.undoAvailableProperty().map(x -> !x));

        rightSideAutoRadioButton.selectedProperty().bindBidirectional(menuAutomatischAusfuehren.selectedProperty());

        rightSideStepButton.disableProperty().bind(menuAutomatischAusfuehren.selectedProperty());
        menuStepButton.disableProperty().bind(menuAutomatischAusfuehren.selectedProperty());

        // Der Code, der sich um die Reparatur der Jumpreferenzen kümmert (FEHLERHAFT!!!)
        codeArea.getParagraphs().sizeProperty().addListener((obsVal, oldVal, newVal) -> {

            synchronized (codeArea.getParagraphs()) {
                boolean lineAdded = oldVal < newVal;    // Unterschied, der ausgeglichen werden soll
                int caretLine = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor();
                int caretColumn = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMinor();

                // Implementierungsfehler in CodeArea ausgleichen!!!!
                if(!lineAdded) {
                    caretLine++;
                    caretColumn--;
                }


                System.out.println("Line Added: " + lineAdded + "\t caretLine: " + caretLine + "\t caretColumn: " + caretColumn);

                if( caretColumn == 0)
                    System.out.println("LINE START");

                for (int i=0; i<newVal; i++) { // Jede Zeile durchgehen (newVal == getParagraphs().length())


                    String x = codeArea.getParagraph(i).getText().toUpperCase();

                    if(x.startsWith("JUMP") || x.startsWith("JEQ") || x.startsWith("JGT") || x.startsWith("JGE") ||
                            x.startsWith("JLT") || x.startsWith("JLE")) {

                            System.out.println("\tJump gefunden:");


                            Scanner sc = new Scanner(x);
                            sc.next();

                            if(sc.hasNextInt()) {


                                int referenceToLine = sc.nextInt(); // Jumpreferenz holen
                                System.out.println("\treferenceToLine: " + (referenceToLine - 1));

                                // Wenn Caret grad an Pos==0 in Zeile ist, zu der gesprungen werden soll,
                                // DANN verhält sich Caret wie als würde er in der Zeile davor stehen
                                // deshalb caretLine--
                                if(codeArea.getCaretColumn() == 0 && caretLine == referenceToLine - 1) {
                                    caretLine--;
                                    System.out.println("IN IF GEFALLEN!! " + caretLine);
                                }
                                System.out.println("\tcaretLine: " + caretLine);


                                // Wenn die angegebene Jumpreferenz hinter dem Caret liegt,
                                // also angepasst werden muss
                                if (referenceToLine - 1 > caretLine) {


                                    // wohin muss die neue Referenz zeigen????
                                    int newReference = referenceToLine + newVal - oldVal;

                                    // an diesem Index in der aktuellen Zeile steht die Jumpreferenz im alten String
                                    int replaceIndex = codeArea.getParagraph(i).getText().indexOf(Integer.toString(referenceToLine));

                                    //  Ersetze die alte Jumpreferenz mit der neuen
                                    codeArea.replaceText(i, replaceIndex, i, replaceIndex + Integer.toString(referenceToLine).length(), Integer.toString(newReference));
                                }
                            } // if sc.nextInt End
                    } // If Jump End
                }   // for End
            }   // Sync End
        });     // Lambda End

    }

    // Hilfsmethode zum Einrichten der Registermaschine
    private void newRegistermaschine() {
        registermaschine = new Registermaschine(this, 16);
        SimpleIntegerProperty[] registers = registermaschine.getR();
        SimpleIntegerProperty A = registermaschine.aProperty();
        SimpleIntegerProperty BZ = registermaschine.BZProperty();

        Bindings.bindBidirectional(rsAkkumulatorTextField.textProperty(), A, new NumberConverter(A, rsAkkumulatorTextField));
        Bindings.bindBidirectional(rsInstructionTextField.textProperty(), BZ, new NumberConverter(BZ, rsInstructionTextField, 1));

        Bindings.bindBidirectional(rsReg1.textProperty(), registers[0], new NumberConverter(registers[0], rsReg1));
        Bindings.bindBidirectional(rsReg2.textProperty(), registers[1], new NumberConverter(registers[1], rsReg2));
        Bindings.bindBidirectional(rsReg3.textProperty(), registers[2], new NumberConverter(registers[2], rsReg3));
        Bindings.bindBidirectional(rsReg4.textProperty(), registers[3], new NumberConverter(registers[3], rsReg4));
        Bindings.bindBidirectional(rsReg5.textProperty(), registers[4], new NumberConverter(registers[4], rsReg5));
        Bindings.bindBidirectional(rsReg6.textProperty(), registers[5], new NumberConverter(registers[5], rsReg6));
        Bindings.bindBidirectional(rsReg7.textProperty(), registers[6], new NumberConverter(registers[6], rsReg7));
        Bindings.bindBidirectional(rsReg8.textProperty(), registers[7], new NumberConverter(registers[7], rsReg8));
        Bindings.bindBidirectional(rsReg9.textProperty(), registers[8], new NumberConverter(registers[8], rsReg9));
        Bindings.bindBidirectional(rsReg10.textProperty(), registers[9], new NumberConverter(registers[9], rsReg10));
        Bindings.bindBidirectional(rsReg11.textProperty(), registers[10], new NumberConverter(registers[10], rsReg11));
        Bindings.bindBidirectional(rsReg12.textProperty(), registers[11], new NumberConverter(registers[11], rsReg12));
        Bindings.bindBidirectional(rsReg13.textProperty(), registers[12], new NumberConverter(registers[12], rsReg13));
        Bindings.bindBidirectional(rsReg14.textProperty(), registers[13], new NumberConverter(registers[13], rsReg14));
        Bindings.bindBidirectional(rsReg15.textProperty(), registers[14], new NumberConverter(registers[14], rsReg15));
        Bindings.bindBidirectional(rsReg16.textProperty(), registers[15], new NumberConverter(registers[15], rsReg16));

    }


    // Wenn man die Checkbox anklickt, die bestimmt, ob man
    @FXML
    public void onAutomatischAusfuehrenClick(ActionEvent e) {
        if(menuAutomatischAusfuehren.isSelected())
            registermaschine.startTimer(1000);
        else
            registermaschine.stopTimer();
    }


    // Wenn man auf die beiden Startknöpfe klickt
    @FXML
    public void onRSStartButtonClick(ActionEvent e) {

        if (rightSideStartButton.getText().equals("Start")) {

            codeArea.editableProperty().set(false);

            rightSideStartButton.setText("Stop");
            menuProgrammStartenButton.setText("Programm stoppen");

            registermaschine.setCode(codeArea.getText());
            registermaschine.prepareAusführung();

            if(rightSideAutoRadioButton.isSelected()) {
                registermaschine.startTimer(1000);
                statusLabel.setText("Registermaschine läuft automatisch");
            }

            statusLabel.setText("Registermaschine läuft. \"Schritt\" drücken");

        } else {

            codeArea.editableProperty().set(true);

            rightSideStartButton.setText("Start");
            menuProgrammStartenButton.setText("Programm starten");

            if(rightSideAutoRadioButton.isSelected()) {
                registermaschine.stopTimer();
            }
            registermaschine.reset();
        }
    }

    @FXML
    public void onRSStepButtonClick(ActionEvent e) {
        registermaschine.einzelschritt();
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

    @Override
    public void updateStatus(String msg) {
        statusLabel.setStyle("-fx-fill: BLUE");
        statusLabel.setText(msg);
    }

    @Override
    public void errorEncountered(String msg, int line) {
        statusLabel.setStyle("-fx-fill: RED");
        statusLabel.setText(msg);

        for(int i=0; i<codeArea.getParagraphs().size(); i++)
            codeArea.setStyle(i, Collections.singletonList("nothing"));

        codeArea.setStyle(line, Collections.singletonList("error"));
    }

    @Override
    public void endEncountered() {
        codeArea.editableProperty().set(true);
        statusLabel.setStyle("-fx-fill: GREEN");
        statusLabel.setText("Programmende");

        for(int i=0; i<codeArea.getParagraphs().size(); i++)
            codeArea.setStyle(i, Collections.singletonList("nothing"));
    }

    @Override
    public void updateHighlighting(int line) {
        System.out.println(line);

        for(int i=0; i<codeArea.getParagraphs().size(); i++)
        codeArea.setStyle(i, Collections.singletonList("nothing"));

        if(line != -1 && line < codeArea.getParagraphs().size()) {
            codeArea.setStyle(line, Collections.singletonList("currentLine"));
        }
    }
}
