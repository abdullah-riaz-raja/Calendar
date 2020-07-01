package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Events {

    //static TableView<EventTable> table = new TableView<>();

    static Stage mainWindow = new Stage();

    public static void display(String[] date) throws SQLException {

        String dateString = date[0] + "-" + date[1] + "-" + date[2];

        System.out.println(dateString + " has been pressed.");

        TableView<EventTable> table = readFrom(dateString);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(20, 20, 20, 20));
        hBox.setSpacing(15);
        hBox.setAlignment(Pos.CENTER);

        Button addEvent = new Button("Add Event");
        addEvent.setOnAction(e -> {
            displayAddEventWindow(dateString);
        });

        Button refresh = new Button("Refresh");         //Fix Refresh. Table doesnt update when new
        refresh.setOnAction(e -> {                        //events are creted
        });

        hBox.getChildren().addAll(addEvent, refresh);
        vBox.getChildren().addAll(table, hBox);



        mainWindow.setTitle("Events");
        mainWindow.setScene(new Scene(vBox, 550, 320));
        mainWindow.getScene().getStylesheets().add("customCSS.css");
        mainWindow.showAndWait();
        mainWindow.centerOnScreen();
    }

    public static void displayAddEventWindow(String date) {

        Stage window = new Stage();

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
                    writeTo(date, title.getText(), detail.getText());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        vBox.getChildren().addAll(prompt, title, detail, enter);
        window.setTitle("Add Event");
        window.setScene(new Scene(vBox, 550, 300));
        window.getScene().getStylesheets().add("customCSS.css");
        window.show();
    }

    public static void writeTo(String date, String title, String detail) throws SQLException {

        String query = "INSERT INTO Events (Date, Title, Detail) VALUES (?, ?, ?);";

        PreparedStatement preStmt = Main.con.prepareStatement(query);
        preStmt.setString(1, date);
        preStmt.setString(2, title);
        preStmt.setString(3, detail);

        preStmt.executeUpdate();
    }

    public static TableView readFrom(String date) throws SQLException {

        TableView<EventTable> table = new TableView<>();;

        TableColumn dateColumn = new TableColumn("Date");
        TableColumn titleColumn = new TableColumn("Title");
        TableColumn detailColumn = new TableColumn("Detail");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        detailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));

        ObservableList<EventTable> eventList = FXCollections.observableArrayList();

        String query = "SELECT * from Events WHERE date = ?";

        System.out.println("idk why this dont work");

        PreparedStatement prepStmt = Main.con.prepareStatement(query);
        prepStmt.setString(1, date);
        ResultSet rs = prepStmt.executeQuery();

        while(rs.next()) {
            eventList.add(new EventTable(rs.getString(1),
                    rs.getString(2), rs.getString(3)));
        }

        table.setItems(eventList);
        table.getColumns().addAll(dateColumn, titleColumn, detailColumn);

        return table;
    }

}
