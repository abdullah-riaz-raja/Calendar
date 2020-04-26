package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    int[] daysInMonth = {0,0,0,31,30,31,30,31,31,30,31,30,31,31,28};

    @Override
    public void start(Stage primaryStage) throws Exception{

        VBox base = new VBox();                                         //base to place nodes on

        //Settings and Current Name/Year
        HBox settings = new HBox();
        Label currentMonthYear = new Label("Month Year");
        settings.getChildren().add(currentMonthYear);
        settings.setPadding(new Insets(10));

        //Displaying Days
        HBox daysHeader = new HBox();
        //daysHeader.setSpacing(90);
        daysHeader.setPadding(new Insets(10));

//        double pref = 150;
//        Label monday = new Label("Monday");
//        Label tuesday = new Label("Tuesday");
//        Label wednesday = new Label("Wednesday");
//        Label thursday = new Label("Thursday");
//        Label friday = new Label("Friday");
//        Label saturday = new Label("Saturday");
//        Label sunday = new Label("Sunday");
//        monday.setPrefWidth(pref);
//        tuesday.setPrefWidth(pref);
//        wednesday.setPrefWidth(pref);
//        thursday.setPrefWidth(pref);
//        friday.setPrefWidth(pref);
//        saturday.setPrefWidth(pref);
//        sunday.setPrefWidth(pref);
//
//        daysHeader.getChildren().addAll(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        Node dayView = drawDays("April", 2020);

        primaryStage.setTitle("Calendar");
        base.getChildren().addAll(settings, dayView);
        primaryStage.setScene(new Scene(base, 1100, 700));
        primaryStage.show();
    }

    public Node drawDays(String month, int year) {

        int yearCalc = 0;
        int fday;                               //first day
        double h;                               //also first day but yet to be modulated by 7
        double monthNum;                        //month Number according to Zeller's formula

        monthNum = monthConv(month);

        //Adjusting for February's 28/29 Days depending on
        //whether its a Leap Year or not
        if (year % 4 == 0 && year % 100 != 0 || year % 4 == 0 && year % 100 == 0 && year % 400 == 0) {
            daysInMonth[14] = 29;
        }

        //This is to adjust for finding the
        //first day of the month according to Zeller's formula
        if (monthNum == 13 || monthNum == 14) {
            yearCalc = year - 1;
        } else if (monthNum <= 12) {
            yearCalc = year;
        }

        h = (1 + Math.floor((13 * (monthNum + 1)/5)) + yearCalc + (yearCalc/4)
                - (yearCalc/100) + (yearCalc/400));
        fday = (int) (h % 7);

        Canvas dayView = new Canvas(1000, 500);
        GraphicsContext gc = dayView.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);

        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");

        double w = 150;
        double height = 50;

        gc.setFont(new Font("Arial", 20));
        for (int i = 0; i < 7; i++) {
            gc.fillText(days.get(i), i*150+20, 20);
        }
        gc.strokeLine(0, 30, 1100, 30);

        gc.strokeRect((fday - 2)*w, 40, w, height);

        System.out.println(fday);
        return dayView;
    }

    public int monthConv(String month) {

        int monthNum = 0;

        switch (month) {
            case "January":
                monthNum = 13;
                break;
            case "February":
                monthNum = 14;
                break;
            case "March":
                monthNum = 3;
                break;
            case "April":
                monthNum = 4;
                break;
            case "May":
                monthNum = 5;
                break;
            case "June":
                monthNum = 6;
                break;
            case "July":
                monthNum = 7;
                break;
            case "August":
                monthNum = 8;
                break;
            case "September":
                monthNum = 9;
                break;
            case "October":
                monthNum = 10;
                break;
            case "November":
                monthNum = 11;
                break;
            case "December":
                monthNum = 12;
                break;
            default:
                System.out.println("Incorrect input.");
        }

        return monthNum;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
