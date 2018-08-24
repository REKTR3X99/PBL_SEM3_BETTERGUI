package Layout;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;
import java.util.*;
import java.util.List;


public class Controller implements Initializable {


    private ObservableList<String> CountryList;
    private ObservableList<String> CapitalList;
    private ObservableList<String> GMTOffset;



    @FXML
     private ComboBox CountryComboBox;

    @FXML
    private Label CapitalDisplayLabel;

    @FXML
    private Label HourTens;

    @FXML
    private Label  HourUnits;

    @FXML
    private Label MinuteTens;

    @FXML
    private Label MinuteUnits;

    @FXML
    private MenuBar Menu;

    @FXML
    private Button AboutButton;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        BufferedReader CountryListReader = null;
        BufferedReader CapitalListReader  = null;
        BufferedReader OffsetListReader = null;

        String CountryLine  = "";
        String CapitalLine = "";
        String OffsetLine = "";

        List<String>CoL = new ArrayList<>();
        List<String>CaL = new ArrayList<>();
        List<String>OfL = new ArrayList<>();


        try
        {
            //Reading data

            CountryListReader = new BufferedReader(new FileReader("Resources/CountryNames.dat"));
            CapitalListReader = new BufferedReader(new FileReader("Resources/CapitalNames.dat"));
            OffsetListReader = new BufferedReader(new FileReader("Resources/TimeZoneOffset.dat"));

        }catch(IOException e) //Catching IOException and alerting the user
        {
            Alert FileReadError = new Alert(Alert.AlertType.ERROR);
            FileReadError.setHeaderText("File(s) are missing");
            FileReadError.setContentText("One or more files that are required are missing");
            FileReadError.showAndWait();
        }

        int Index = 0;

        while(CountryLine!= null && CapitalLine!= null && OffsetLine != null && Index <=99)
        {
            try {
                CountryLine = CountryListReader.readLine();
                CapitalLine = CapitalListReader.readLine();
                OffsetLine = OffsetListReader.readLine();

            } catch (IOException e) {

                Alert FileReadError = new Alert(Alert.AlertType.ERROR);
                FileReadError.setHeaderText("File(s) cannot be read");
                FileReadError.setContentText("One or more files that are required cannot be read");
                FileReadError.showAndWait();
            }

            CoL.add(Index, CountryLine);
            CaL.add(Index, CapitalLine);
            OfL.add(Index, OffsetLine);

            Index++;


        }

        CountryList = FXCollections.observableArrayList(CoL);
        CapitalList = FXCollections.observableArrayList(CaL);
        GMTOffset = FXCollections.observableArrayList(OfL);

        CountryComboBox.setValue("Afghanistan");
        CountryComboBox.setItems(CountryList);


    }

    @FXML
    public void Capital(ActionEvent e)
    {

        CapitalDisplayLabel.setText(CapitalList.get((CountryComboBox.getSelectionModel().getSelectedIndex())));
        DisplayTime();

    }

    @FXML
    void DisplayTime()
    {
        //Setting the Calendar to read GMT Time
        Calendar CalToGetTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));


        int GMTHour = CalToGetTime.get(Calendar.HOUR_OF_DAY); //Get the current GMT Hour
        int GMTMinute = CalToGetTime.get(Calendar.MINUTE); //Get the current GMT time

        String checker = GMTOffset.get(CountryComboBox.getSelectionModel().getSelectedIndex());
        String HourString; //To Store the hours
        String MinuteString; //To Store the minutes
        boolean DoesExist = true;

        //Checking if the checker contains "UTC" or not
        /*
         * If It does have UTC it means that the time cannot be determined
         * */
        if(!checker.contains("UTC"))
        {
            /*
             * Format of the time in TimeZoneOffset.dat is (sign)X1X2.Y1Y2
             *
             * where sign gives the position relative to GMT, + means its ahead and - means its behind
             * X1 and X2 correspond to the hours
             * Y1 and Y2 correspond to the minutes
             *
             * substring of 1,3 is X1 and X2
             * substring of 4,6 is Y1 and Y2
             * */
            HourString = checker.substring(1,3);
            MinuteString = checker.substring(4,6);
            DoesExist = true; //Saying that the given value exists in the Zone Offset data
        }else
        {
            DoesExist = false;
            HourString = "0";
            MinuteString ="0";
        }


        /*
         * Bottom code checks if the start of the checker.
         * Checker equals the data which has the index of the selected item from the combobox
         * and the ZoneOffsetData.
         *
         * It checks if the checker starts with which sign and if the specified value exists in the given data or if its "UTC"
         * for which the time cannot be determined.
         */


        /*
         * if checker starts with "+" that is the read data starts with a "+" then add the number of hours of offset
         * which is in the GMTHour to the given hour.
         *
         * After this if the number of Hours is more than 25, then negate the hours by 24 which would bring them in range
         * Same with the number of minutes
         */


        /*
         * if checker starts with "-" the selected country is behind GMT by X1X2 amount of hours.
         * for this negate the number of hours ( Offset) from GMT.
         *
         * If the number is less than 0 then add 24 to it to bring it to the proper range
         * same with the number of minutes
         * */


        if(checker.startsWith("+") && DoesExist) //If the country is ahead GMT and does exists in the ZoneOffsetData
        {
            try
            {
                // offsetting the given GMTHour from the calendar by the Zone Offset from GMT
                //For some reason  if I don't add 1 the time remains an hour behind so there's a 1
                //Also converting HourString from String to Int
                GMTHour = GMTHour + Integer.parseInt(HourString);

                //Offsetting the minute and adding it to the GMTMinute.
                //Also parsing the value of MinuteString to convert from String to Int
                GMTMinute = GMTMinute + Integer.parseInt(MinuteString);

                if(GMTHour >= 24) //Checking the addition exceeds or equals 24 hours
                {
                    GMTHour = GMTHour - 24; //Negating by 24 incase it exceeds
                }

                if(GMTMinute >= 60) //Checking if minutes exceeds or equals 60 minute
                {
                    GMTMinute = GMTMinute - 60; //If it does, negate by 60
                }
            }catch(Exception e)
            {
                final  JPanel panel = new JPanel(); //JPanel for error
                JOptionPane.showMessageDialog(panel,"Parsing Error for countries Ahead of GMT", "Error", JOptionPane.ERROR_MESSAGE);  //Displaying error
            }
        }else if(checker.startsWith("-") && DoesExist) //If the country is behind GMT
        {
            try
            {
                GMTHour = GMTHour - Integer.parseInt(HourString);
                GMTMinute = GMTMinute - Integer.parseInt(MinuteString);

                if(GMTHour < 0)
                {
                    GMTHour = GMTHour + 24;
                }

                if(GMTMinute < 0)
                {
                    GMTMinute = GMTMinute + 60;
                }
            }catch(Exception e)
            {
                final  JPanel panel = new JPanel(); //JPanel for error
                JOptionPane.showMessageDialog(panel,"Parsing error for countries behind GMT", "Error", JOptionPane.ERROR_MESSAGE);  //Displaying error
            }
        }
        System.out.println(GMTHour + ":" + GMTMinute);

        HourUnits.setText(String.valueOf(GMTHour % 10));
        GMTHour/=10;
        HourTens.setText(String.valueOf(GMTHour));

        MinuteUnits.setText(String.valueOf(GMTHour % 10));
        GMTMinute/=10;
        MinuteTens.setText(String.valueOf(GMTMinute));

    }

    @FXML
    void close(ActionEvent e)
    {
        Platform.exit();
    }

    @FXML
    void AddItem(ActionEvent e) throws Exception
    {

        Stage AddLayoutStage = new Stage();

        Parent root = FXMLLoader.load(getClass().getResource("AddLayout.fxml"));


        Scene scene = new Scene(root, 400, 600);
        scene.setRoot(root);
        AddLayoutStage.setTitle("Set Value");
        AddLayoutStage.setScene(scene);
        AddLayoutStage.show();


    }

    @FXML
    void About(ActionEvent e)
    {
        Alert AboutField = new Alert(Alert.AlertType.INFORMATION);
        AboutField.setHeaderText("Team");
        AboutField.setContentText("Ajay Nair \n Parth Nair\n Gokul Chathankulam");
        AboutField.showAndWait();
    }

}
