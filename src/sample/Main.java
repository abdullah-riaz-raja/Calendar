package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        VBox base = new VBox();                                         //base to place nodes on

        HBox monthHeader = new HBox();
        

        //Displaying Days
        HBox daysHeader = new HBox();
        daysHeader.setSpacing(90);
        daysHeader.setPadding(new Insets(10));

        Label monday = new Label("Monday");
        Label tuesday = new Label("Tuesday");
        Label wednesday = new Label("Wednesday");
        Label thursday = new Label("Thursday");
        Label friday = new Label("Friday");
        Label saturday = new Label("Saturday");
        Label sunday = new Label("Sunday");

        daysHeader.getChildren().addAll(monday, tuesday, wednesday, thursday, friday, saturday, sunday);





        primaryStage.setTitle("Calendar");
        base.getChildren().add(daysHeader);
        primaryStage.setScene(new Scene(base, 1000, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
