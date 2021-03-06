package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {

    //Global Variables
    int[] daysInMonth = {0,0,0,31,30,31,30,31,31,30,31,30,31,31,28};
    ArrayList<String> days = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday"));
    ArrayList<String> months = new ArrayList<>(Arrays.asList("January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"));

    static Connection con;

    static int[] todayDate = getTodayDate();

    @Override
    public void start(Stage primaryStage) throws SQLException {

        //Connects to the Database
        con = DBConnection.startConnection();

        //Creating a layout for the startup page
        GridPane dateEntry = new GridPane();
        dateEntry.setHgap(5);
        dateEntry.setVgap(10);
        dateEntry.setPadding(new Insets(25, 20, 30, 45));

        //Drop down menu for Month Selection
        Label monthEntry = new Label("Month: ");
        ComboBox<String> monthComboBox = new ComboBox<>();
        for (String month : months) {
            monthComboBox.getItems().add(month);
        }
        dateEntry.add(monthEntry, 0, 0);
        dateEntry.add(monthComboBox, 1, 0);

        //Year Entry Field
        Label yearEntry = new Label("Year: ");
        TextField yearField = new TextField();
        dateEntry.add(yearEntry, 0, 1);
        dateEntry.add(yearField, 1, 1);

        //Enter Button
        Button enter = new Button("Enter");
        enter.setOnAction(e -> display(primaryStage,monthComboBox.getValue(), Integer.parseInt(yearField.getText())));
        dateEntry.add(enter, 0, 2);

        //Today Button - Takes the user to calendar for Today's month
        Button today = new Button("Today");
        today.setOnAction(e -> display(primaryStage, months.get(todayDate[1]-1), todayDate[0]));
        dateEntry.add(today, 1, 2);

        //Setting up stage
        primaryStage.setTitle("Enter Month and Year");
        primaryStage.setScene(new Scene(dateEntry, 300, 150));
        primaryStage.getScene().getStylesheets().add("customCSS.css");
        primaryStage.show();
    }

    public void display(Stage stage, String month, int year) {

        //Vertical base to place nodes on
        VBox base = new VBox();

        //Navigation and Current Name/Year (Current is month/year currently being viewed by the user)
        HBox settings = new HBox();
        settings.setPadding(new Insets(20, 45, 15, 45));
        settings.setSpacing(20);
        settings.setAlignment(Pos.CENTER_LEFT);

        //Textfield doubles as viewing current month/year and the place to search out any
        //month/year combo user wishes to go to
        TextField currentMonth = new TextField(month);
        TextField currentYear = new TextField(Integer.toString(year));
        currentMonth.setMaxHeight(40);
        currentYear.setMaxHeight(40);

        //Takes the user to the typed out month/year combo in the adjacent textfield
        Button goTo = new Button("Go To");
        goTo.setOnAction(e -> display(stage, currentMonth.getText(), Integer.parseInt(currentYear.getText())));

        //Takes the user to the next month
        Button next = new Button("Next");
        next.setOnAction(e -> {
            boolean match = false;
            for (int i = 0; i < 11; i++) {
                if (months.get(i).equals(month)) {
                    display(stage, months.get(i+1), year);
                    match = true;
                }
            }
            //if the last month of the year is reached, takes the user to the first month of next year
            if (!match) {
                display(stage, months.get(0), year + 1);
            }
        });

        //Takes the user to the previous month
        Button prev = new Button("Previous");
        prev.setOnAction(e -> {
            boolean match = false;
            for (int i = 1; i < 12; i++) {
                if (months.get(i).equals(month)) {
                    display(stage, months.get(i-1), year);
                    match = true;
                }
            }
            //if the first month of the year is reached, takes the user to the last month of previous year
            if (!match) {
                display(stage, months.get(11), year - 1);
            }
        });

        //Takes the user to the today's month and year
        Button today = new Button("Today");
        today.setOnAction(e -> display(stage, months.get(todayDate[1]-1), todayDate[0]));

        //Exits the application
        Button exit = new Button("Exit");
        exit.setStyle("-fx-background-color: #CF1E25");
        exit.setOnAction(e -> {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        //Shows the current date in a big font
        Label currentDate = new Label("    " + month + " " + year);
        currentDate.getStyleClass().add("currentDate");

        settings.getChildren().addAll(currentMonth, currentYear, goTo, prev, next, today, exit);

        //Draws the list of days
        Node daysHeader = drawDays();
        //Draws the month view of the current month
        Node monthView = drawMonth(month, year);

        //Setting up Stage
        stage.setTitle("Calendar");
        base.getChildren().addAll(settings, currentDate, daysHeader, monthView);
        stage.setScene(new Scene(base, 1100, 750));
        stage.getScene().getStylesheets().add("customCSS.css");
        stage.show();
    }

    //Returns an array with today's date in yyyy/mm/dd format
    public static int[] getTodayDate() {

        int[] returnVals = {0, 0, 0};

        LocalDate date = LocalDate.now();

        returnVals[0] = date.getYear();
        returnVals[1] = date.getMonthValue();
        returnVals[2] = Integer.parseInt(date.toString().substring(9, 10));

        return returnVals;
    }

    public double[] initializeMonth(String month, int year) {

        int yearCalc = 0;
        int fday;                               //first day
        double h;                               //also first day but yet to be modulated by 7
        double monthNum;                        //month Number according to Zeller's formula
        double[] returnVals = {0, 0};        //array to be returned containing above values

        monthNum = monthConv(month);
        returnVals[0] = monthNum;

        //Adjusting for February's 28/29 Days depending on
        //whether its a Leap Year or not
        if (year % 4 == 0 && year % 100 != 0 || year % 4 == 0 && year % 100 == 0 && year % 400 == 0) {
            daysInMonth[14] = 29;
        } else {
            daysInMonth[14] = 28;
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

        int newFday = fdayConv(fday);

        returnVals[1] = newFday;

        return returnVals;
    }

    public Node drawDays() {

        Canvas dayView = new Canvas(1100, 90);
        GraphicsContext gcDayView = dayView.getGraphicsContext2D();
        gcDayView.setStroke(Color.BLACK);
        gcDayView.setFill(Color.WHITE);
        gcDayView.setLineWidth(1.2);

        gcDayView.setFont(new Font("Arial", 20));
        for (int i = 0; i < 7; i++) {
            gcDayView.fillText(days.get(i), i*150+20+30, 20+10+10);
        }
        gcDayView.strokeLine(0, 30+20, 1100, 30+20);

        HBox dayViewCentered = new HBox();
        dayViewCentered.setAlignment(Pos.CENTER);
        dayViewCentered.getChildren().add(dayView);

        return dayViewCentered;
    }

    public Node drawMonth(String month, int year) {

        //Values for calculating month view (month length, starting day of the month etc)
        double[] values = initializeMonth(month, year);     //contains monthNum, yearCalc and newFday
        double monthNum = values[0];                        //month Number according to Zeller's formula
        int newFday = (int) values[1];                      //first day

        String[] date = {"", month, Integer.toString(year)}; //dd/month//year format

        //Keeps track of how many days are created
        int daysCounter = 1;

        GridPane monthView = new GridPane();
        monthView.setAlignment(Pos.CENTER);
        monthView.setPadding(new Insets(25, 10, 45, 10));
        monthView.getStyleClass().add("days-button");

        //Initializes the days as buttons
        ArrayList<Button> buttons = new ArrayList<>();
        for (int i = 0; i < 50; i++) {                 //Figure this out. 50 Buttons shouldn't be needed
            buttons.add(new Button());
            buttons.get(i).setPrefWidth(150);
            buttons.get(i).setPrefHeight(80);

            int finalI = i;
            buttons.get(i).setOnAction(e -> {
                date[0] = buttons.get(finalI).getText();
                try {
                    Events.start(date);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }

        //Draws the first row with differing starting days of each month
        if (newFday == 6) {
            for (int i = 0; i < 7; i++) {
                buttons.get(i).setText(Integer.toString(daysCounter));
                daysCounter++;
            }
        } else {
            for (int i = 0; i < 7 - newFday - 1; i++) {
                buttons.get(newFday + i + 1).setText(Integer.toString(daysCounter));
                daysCounter++;
            }
        }

        //Adds the first row of days to the to-be-returned node
        for (int i = 0; i < 7; i++) {
            monthView.add(buttons.get(i), i, 0);
        }

        //Adds the rest of the days (buttons) to the to-be-returned node
        int xx = 6;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (daysCounter > daysInMonth[(int) monthNum]) {
                    break;
                }

                buttons.get(xx+1).setText(Integer.toString(daysCounter));
                monthView.add(buttons.get(xx+1), j, i+1);

                xx++;
                daysCounter++;
            }
        }

        return monthView;
    }

    public int fdayConv(int fday) {

        int newFday = 0;

        switch (fday) {
            case 0:  newFday = 5;  break;
            case 1:  newFday = 6;  break;
            case 2:  newFday = 0;  break;
            case 3:  newFday = 1;  break;
            case 4:  newFday = 2;  break;
            case 5:  newFday = 3;  break;
            case 6:  newFday = 4;  break;
            default: System.out.println("Incorrect input.");
        }

        return newFday;
    }

    public int monthConv(String month) {

        int monthNum = 0;

        switch (month) {
            case "January":     monthNum = 13; break;
            case "February":    monthNum = 14; break;
            case "March":       monthNum = 3;  break;
            case "April":       monthNum = 4;  break;
            case "May":         monthNum = 5;  break;
            case "June":        monthNum = 6;  break;
            case "July":        monthNum = 7;  break;
            case "August":      monthNum = 8;  break;
            case "September":   monthNum = 9;  break;
            case "October":     monthNum = 10; break;
            case "November":    monthNum = 11; break;
            case "December":    monthNum = 12; break;
            default: System.out.println("Incorrect input.");
        }

        return monthNum;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
