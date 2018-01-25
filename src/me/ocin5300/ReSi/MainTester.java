package me.ocin5300.ReSi;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import me.ocin5300.ReSi.logik.Registermaschine;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class MainTester extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new FlowPane();
        TableView<Registermaschine> table = new TableView<>();

        table.getColumns().addAll(new TableColumn<Registermaschine, String>("1"));
        TableColumn<Registermaschine, Integer> tc = new TableColumn<>("2");
        table.getColumns().addAll(tc);

        Registermaschine registermaschine = new Registermaschine(16);

        table.getItems().addAll(registermaschine);

        tc.setCellFactory(registermaschineTableColumn -> {
            return new TableCell<Registermaschine,Integer>() {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        FlowPane fp = new FlowPane();
                        fp.getChildren().addAll(new Label("A1:"));
                        TextField tf = new TextField();
                        tf.setMaxWidth(50);

                        fp.setMaxWidth(70);
                        Bindings.bindBidirectional(tf.textProperty(), registermaschine.getR().get(0), new StringConverter<Number>() {
                            @Override
                            public String toString(Number number) {
                                return number.toString();
                            }

                            @Override
                            public Number fromString(String s) {

                                try {
                                    return Integer.parseInt(s);
                                } catch (NumberFormatException e) {
                                    return registermaschine.getR().get(0).get();
                                }
                            }
                        });

                        fp.getChildren().addAll(tf);

                        setGraphic(fp);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });
        tc.setCellValueFactory((TableColumn.CellDataFeatures<Registermaschine, Integer> registermaschineStringCellDataFeatures) -> {
            return registermaschineStringCellDataFeatures.getTableView().getItems().get(0).getR().get(0).asObject();
        });

        tc.setMaxWidth(80);
        CodeArea ca = new CodeArea();
        VirtualizedScrollPane<CodeArea> scrp = new VirtualizedScrollPane<>(ca);
        ca.setMaxSize(scrp.getWidth(), scrp.getHeight());
        ca.setParagraphGraphicFactory(LineNumberFactory.get(ca));
        scrp.setMaxSize(100, 600);
        pane.getChildren().addAll(table, new Button("Hi"), new Label("LOOOL"), scrp);
        pane.setPrefSize(600, 600);
        pane.setMinSize(600, 600);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

    }
}
