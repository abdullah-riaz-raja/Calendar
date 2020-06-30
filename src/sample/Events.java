package sample;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Events {

    public static void display() {

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(10);

        Label prompt = new Label("Enter your event details here:");

        TextField eventField = new TextField();
        eventField.setMinHeight(100);
        eventField.setMinWidth(500);

        Button enter = new Button("Enter");
        enter.setOnAction(e -> {
            if (!eventField.getText().equals("")) {
                System.out.println("Event Created: " + eventField.getText());
            }
        });

        vBox.getChildren().addAll(prompt, eventField, enter);

        Stage window = new Stage();

        window.setTitle("Events");
        window.setScene(new Scene(vBox, 550, 200));
        window.getScene().getStylesheets().add("customCSS.css");
        window.showAndWait();

    }


}
