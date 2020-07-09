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

    static String date = "";

    public static void start(String[] dateArray) throws SQLException {

        Stage primaryStage = new Stage();

        date = dateArray[0] + "-" + dateArray[1] + "-" + dateArray[2];

        System.out.println(date + " has been pressed.");

        displayEvents(primaryStage);
    }

    public static void onClick(TableView<EventTable> table) throws SQLException {
        if (table.getSelectionModel().getSelectedItem() != null) {
            EventTable selected = table.getSelectionModel().getSelectedItem();
            System.out.println(selected.getTitle());

            String title = selected.getTitle();
            String date = selected.getDate();

            clear(date, title);
        }
    }

    public static TableView<EventTable> createTable() throws SQLException {
        TableView<EventTable> table = new TableView<>();

        TableColumn dateColumn = new TableColumn("Date");
        TableColumn titleColumn = new TableColumn("Title");
        TableColumn detailColumn = new TableColumn("Detail");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        detailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));

        ObservableList<EventTable> eventList = FXCollections.observableArrayList();

        String query = "SELECT * from Events WHERE date = ?";

        PreparedStatement prepStmt = Main.con.prepareStatement(query);
        prepStmt.setString(1, date);
        ResultSet rs = prepStmt.executeQuery();

        while(rs.next()) {
            eventList.add(new EventTable(rs.getString(1),
                    rs.getString(2), rs.getString(3)));
        }

        table.setItems(eventList);
        table.getColumns().addAll(titleColumn, detailColumn);

        return table;
    }

    public static void displayEvents(Stage stage) throws SQLException {

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(15);

        HBox options = new HBox();
        options.setPadding(new Insets(20, 20, 20, 20));
        options.setSpacing(15);
        options.setAlignment(Pos.CENTER);

        TableView<EventTable> table = createTable();
        table.setMinHeight(400);
        table.setMinWidth(500);

        HBox tableHBox = new HBox();
        tableHBox.setAlignment(Pos.CENTER);
        tableHBox.getChildren().add(table);

        Button addEvent = new Button("Add Event");
        addEvent.setOnAction(e -> displayAddEventWindow(stage));

        Button clearAll = new Button("Clear All");
        clearAll.setOnAction(e -> {
            try {
                clearAllEvents();
                displayEvents(stage);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button clear = new Button("Clear");
        clear.setOnAction(e -> {
            try {
                onClick(table);
                displayEvents(stage);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Label currentDate = new Label(date);
        currentDate.getStyleClass().add("currentDate");

        options.getChildren().addAll(addEvent, clearAll, clear);
        vBox.getChildren().addAll(currentDate, tableHBox, options);

        stage.setTitle("Events");
        stage.setScene(new Scene(vBox, 550, 600));
        stage.getScene().getStylesheets().add("customCSS.css");
        stage.show();
    }

    public static void displayAddEventWindow(Stage stage) {

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(10);

        Label addEvent = new Label("Add Event");
        addEvent.getStyleClass().add("currentDate");

        TextField title = new TextField();
        title.setPromptText("Enter title here");
        title.setMinWidth(500);

        TextField detail = new TextField();
        detail.setPromptText("Enter details here");
        detail.setMinHeight(100);
        detail.setMinWidth(500);
        detail.setStyle("-fx-alignment: top-left");

        Button enter = new Button("Enter");
        //enter.setStyle("-fx-background-color: #00AB66"); eh, dk if I should have dif colored buttons all over
        enter.setOnAction(e -> {
            if (!title.getText().equals("")) {
                System.out.println("Event Created.");
                try {
                    writeTo(title.getText(), detail.getText());
                    displayEvents(stage);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        vBox.getChildren().addAll(addEvent, title, detail, enter);
        stage.setTitle("Add Event");
        stage.setScene(new Scene(vBox, 550, 300));
        stage.getScene().getStylesheets().add("customCSS.css");
        stage.show();
    }

    public static void writeTo(String title, String detail) throws SQLException {

        String query = "INSERT INTO Events (Date, Title, Detail) VALUES (?, ?, ?);";

        PreparedStatement preStmt = Main.con.prepareStatement(query);
        preStmt.setString(1, date);
        preStmt.setString(2, title);
        preStmt.setString(3, detail);

        preStmt.executeUpdate();
    }

    public static void clear(String date, String title) throws SQLException {

        String update = "DELETE FROM Events WHERE date = ? AND title = ?";

        PreparedStatement preStmt = Main.con.prepareStatement(update);
        preStmt.setString(1, date);
        preStmt.setString(2, title);

        preStmt.executeUpdate();
    }

    public static void clearAllEvents() throws SQLException {

        String update = "DELETE FROM Events WHERE date = ?";

        PreparedStatement preStmt = Main.con.prepareStatement(update);
        preStmt.setString(1, date);

        preStmt.executeUpdate();
    }


}
