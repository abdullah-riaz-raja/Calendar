package sample;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Events {

    public static void display(String[] date) {

        String dateString = date[0] + "-" + date[1] + "-" + date[2];

        System.out.println(dateString + " has been pressed.");

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(10);

        Label prompt = new Label("Enter your event details here:");

        TextField title = new TextField();
        title.setMinWidth(500);

        TextField detail = new TextField();
        detail.setMinHeight(100);
        detail.setMinWidth(500);

        Button enter = new Button("Enter");
        enter.setOnAction(e -> {
            if (!title.getText().equals("")) {
                System.out.println("Event Created.");
                try {
                    writeTo(dateString, title.getText(), detail.getText());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        vBox.getChildren().addAll(prompt, title, detail, enter);

        Stage window = new Stage();

        window.setTitle("Events");
        window.setScene(new Scene(vBox, 550, 300));
        window.getScene().getStylesheets().add("customCSS.css");
        window.showAndWait();

    }

    public static void writeTo(String date, String title, String detail) throws SQLException {

        String query = "INSERT INTO Events (Date, Title, Detail) VALUES (?, ?, ?);";

        PreparedStatement preStmt = Main.con.prepareStatement(query);
        preStmt.setString(1, date);
        preStmt.setString(2, title);
        preStmt.setString(3, detail);

        preStmt.executeUpdate();
    }

//    public static void readFrom(String date) throws SQLException {
//
//
//    }

}
