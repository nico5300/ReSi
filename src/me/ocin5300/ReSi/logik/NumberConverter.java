package me.ocin5300.ReSi.logik;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class NumberConverter extends StringConverter<Number> {

    private TextField textField;
    private SimpleIntegerProperty replacementValue;
    private int difference = 0;

    public NumberConverter(SimpleIntegerProperty ersatzwert, TextField tf, int diff) {
        difference = diff;
        textField = tf;
        replacementValue = ersatzwert;
    }

    public NumberConverter(SimpleIntegerProperty ersatzwert, TextField tf) {
        textField = tf;
        replacementValue = ersatzwert;
    }

    @Override
    public String toString(Number number) {
        return Integer.toString(number.intValue() + difference);
    }

    @Override
    public Number fromString(String s) {
        try {
            if(s.equals("-") || s.isEmpty()) {
                textField.positionCaret(textField.getText().length());
                return 0;
            }
            return Integer.parseInt(s) - difference;
        } catch (NumberFormatException e) {
            throwErrorMessage("Fehlerhafte Eingabe", "Die Eingabe " + s + " ist keine Zahl.\n" +
                    "In dieses Feld dürfen nur\nZahlen eingesetzt werden!");
            textField.setText(replacementValue.getValue().toString());
            return replacementValue.getValue();
        }
    }

    private static void throwErrorMessage(String heading, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(heading);
        alert.setContentText(text);
        alert.show();
    }
}
