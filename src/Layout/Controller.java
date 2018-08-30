package Layout;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller implements Initializable {


    private ObservableList<String> CountryList; //Lists for all the Countries in the list
    private ObservableList<String> CapitalList; //List for all the Capitals corresponding to the country
    private ObservableList<String> GMTOffset; //Offset for all the country
    private ObservableList<String> CurrencyData; //Currency data



    @FXML
    private Pane BasePane;

    @FXML
     private ComboBox CountryComboBox; //ComboBox for selecting the country

    @FXML
    private Label CapitalDisplayLabel; //Displays the Capital of the country

    @FXML
    private Label HourTens; //Displays the First number of the Clock which is in 24 hour format

    @FXML
    private Label  HourUnits; //Displays second number

    @FXML
    private Label MinuteTens; //Displays first digit of Minute

    @FXML
    private Label MinuteUnits; //Displays second digit of Minute

    @FXML
    private Label SecondTens; //Displays the first digit of Seconds

    @FXML
    private Label SecondUnits; //Displays the second digit of Seconds

    @FXML
    private ComboBox CurrencyFrom; //To Choose the currency from

    @FXML
    private ComboBox CurrencyTo; //To Choose where to convert the currency

    @FXML
    private TextField CurrencyFromText;

    @FXML
    private Label CurrencyToLabel;

    @FXML
    private Button CurrencyConvertButton;



    //Setting the Calendar to read GMT Time
    private Calendar CalToGetTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));


    //Initializing the frame and adding all the names of countries
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        BufferedReader CountryListReader = null; //BufferedReader for Country
        BufferedReader CapitalListReader  = null;  //BufferedReader for Capital
        BufferedReader OffsetListReader = null; //BufferedReader for the GMT Offset List
        BufferedReader CurrencyReader = null;
        //Set of Strings which take a temporary input
        String CountryLine  = "";
        String CapitalLine = "";
        String OffsetLine = "";
        String CurrencyLine = "";


        //Array for temporary storage
        List<String>CoL = new ArrayList<>();
        List<String>CaL = new ArrayList<>();
        List<String>OfL = new ArrayList<>();
        List<String>CdL = new ArrayList<>();



        try
        {
            //Reading data
            CountryListReader = new BufferedReader(new FileReader("Resources/CountryNames.dat")); //Reading Country data
            CapitalListReader = new BufferedReader(new FileReader("Resources/CapitalNames.dat")); //Reading Capital data
            OffsetListReader = new BufferedReader(new FileReader("Resources/TimeZoneOffset.dat")); //Reading the offset data
            CurrencyReader = new BufferedReader(new FileReader("Resources/CurrencyData.dat"));

        }catch(IOException e) //Catching IOException and alerting the user
        {
            Alert FileReadError = new Alert(Alert.AlertType.ERROR);
            FileReadError.setHeaderText("File(s) are missing");
            FileReadError.setContentText("One or more files that are required are missing");
            FileReadError.showAndWait();
        }

        int Index = 0; //Index for the List

        //Reading every line from each of the list and assigning it to a temporary variable <File-ContentLine>
        while(CountryLine!= null && CapitalLine!= null && OffsetLine != null && Index <=99) //Going till any of the lines go null and Index is less than 99
            //Note : Index has to be explicitly specified to be less than  99 or else the List will have a blank entry from file termination
        {
            try {
                CountryLine = CountryListReader.readLine(); //Read a line from the Country List
                CapitalLine = CapitalListReader.readLine(); //Read from Capital
                OffsetLine = OffsetListReader.readLine(); //Read from the GMT Offset table
                CurrencyLine = CurrencyReader.readLine();

            } catch (IOException e) { //Catching any IO exceptions thrown and alerting the user

                Alert FileReadError = new Alert(Alert.AlertType.ERROR);
                FileReadError.setHeaderText("File(s) cannot be read");
                FileReadError.setContentText("One or more files that are required cannot be read");
                FileReadError.showAndWait();
            }

            //Adding the read lines into a Temporary Array List
            CoL.add(Index, CountryLine);
            CaL.add(Index, CapitalLine);
            OfL.add(Index, OffsetLine);

            try {
                if (!CdL.contains(String.valueOf(CurrencyLine))) {
                    CdL.add(CurrencyLine);
                }
            }catch(Exception E)
            {
                System.err.println(E); //err to print errors
            }

            Index++; //Incrementing the Index

            /*
            *
            * Note : Index isn't necessary for the proper generation of the List.
            * I just added it so that it doesn't mess the order just to be sure.
            * I will probably remove this in the final version
            *
            * */


        }

        //Converting the ArrayList into ObservableArrayList since that's what javafx requires
        CountryList = FXCollections.observableArrayList(CoL);
        CapitalList = FXCollections.observableArrayList(CaL);
        GMTOffset = FXCollections.observableArrayList(OfL);

        Collections.sort(CdL);
        CurrencyData = FXCollections.observableArrayList(CdL);

        //Setting the CountryList as an Item.
        CountryComboBox.setItems(CountryList);

        //Setting The currency Lists
        CurrencyFrom.setItems(CurrencyData);

        CurrencyTo.setItems(CurrencyData);


        //Setting Default value to Afghanistan or it would just show a blank entry
        CountryComboBox.setValue("Afghanistan");

        CurrencyFrom.setValue("AFN");

        CurrencyTo.setValue("AFN");

    }

    @FXML
    public void Capital(ActionEvent e)
    {

        //Setting the label text by getting the index of the country and matching it to the same index in the capital list
        CapitalDisplayLabel.setText(CapitalList.get((CountryComboBox.getSelectionModel().getSelectedIndex())));


        //Make this real time so that the time also changes.
        DisplayTime(); //Display whatever the time is in the given region

    }

    @FXML
    void DisplayTime()
    {
        int GMTHour = CalToGetTime.get(Calendar.HOUR_OF_DAY); //Get the current GMT Hour
        int GMTMinute = CalToGetTime.get(Calendar.MINUTE); //Get the current GMT time
        int GMTSeconds = CalToGetTime.get(Calendar.SECOND);



        String checker = GMTOffset.get(CountryComboBox.getSelectionModel().getSelectedIndex());
        String HourString; //To Store the hours
        String MinuteString; //To Store the minutes
        boolean DoesExist; //Initially is true

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
                Alert ParsingErrorAheadGMT = new Alert(Alert.AlertType.ERROR);//Displaying error

                ParsingErrorAheadGMT.setHeaderText("Parsing Error");
                ParsingErrorAheadGMT.setContentText("An error have occurred while parsing the timezones behind GMT ");
                ParsingErrorAheadGMT.showAndWait();

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
                Alert ParsingErrorBehindGMT = new Alert(Alert.AlertType.ERROR);//Displaying error

                ParsingErrorBehindGMT.setHeaderText("Parsing Error");
                ParsingErrorBehindGMT.setContentText("An error have occurred while parsing the timezones behind GMT ");
                ParsingErrorBehindGMT.showAndWait();

            }
        }


        HourUnits.setText(String.valueOf(GMTHour % 10));
        GMTHour/=10;
        HourTens.setText(String.valueOf(GMTHour));

        MinuteUnits.setText(String.valueOf(GMTHour % 10));
        GMTMinute/=10;
        MinuteTens.setText(String.valueOf(GMTMinute));

        SecondUnits.setText(String.valueOf(GMTSeconds % 10));
        GMTSeconds/=10;
        SecondTens.setText(String.valueOf(GMTSeconds));

        new Thread(this::Updater).start();
        

    }

    void Updater()
    {

        Platform.runLater((new Runnable() {
            @Override
            public void run() {

                long MiliSecond;
                long Seconds;

                while(true) {
                    MiliSecond = System.currentTimeMillis();
                    Seconds = (MiliSecond / 1000) % 60;//TimeUnit.MILLISECONDS.toSeconds(MiliSecond);

                    System.out.println(Seconds);

                    SecondUnits.setText(String.valueOf(Seconds % 10));
                    Seconds /= 10;
                    SecondTens.setText(String.valueOf(Seconds));

                    try {
                        Thread.sleep(1000);
                    } catch (Exception UnhandledException) {
                        System.err.println(UnhandledException);
                    }
                }

            }
        }));
    }



    @FXML
    void CurrencyConvertFunction(ActionEvent e)
    {


        //Checking if the given input is only text or not.

        //If given input is not matching numbers 0 to 9, then its an invalid input and set text to 1
        if(!CurrencyFromText.getText().matches("[0-9]+"))
        {
            Alert WrongEntry = new Alert(Alert.AlertType.ERROR);
            WrongEntry.setHeaderText("Wrong Value inserted");
            WrongEntry.setContentText("Please Enter a proper value and continue");
            WrongEntry.showAndWait();

            CurrencyFromText.setText("1");

        }


        URL FinanceURL; //URL to which the link parsed will be opened


        int IndexCurrencyTo= CurrencyTo.getSelectionModel().getSelectedIndex(); //Get the Currency which we have
        int IndexCurrencyFrom = CurrencyFrom.getSelectionModel().getSelectedIndex(); //Get the currency to which we have to convert

        String CurrencyToConvert = CurrencyData.get(IndexCurrencyTo);
        String CurrencyFromConvert = CurrencyData.get(IndexCurrencyFrom);

        String ConvertedAmount =  null;
        float FinalAmount = 0.0f;

        try {
            //Open Link
            FinanceURL = new URL("http://free.currencyconverterapi.com/api/v5/convert?q="+CurrencyFromConvert+"_"+ CurrencyToConvert+"&compact=y");

            //Read contents of link
            BufferedReader URLReturnDataReader = new BufferedReader(new InputStreamReader(FinanceURL.openStream()));

            //ConvertedAmount basically has the entire contents of the webpage
            ConvertedAmount = URLReturnDataReader.readLine();
        }catch(Exception ParsingException)
        {
            Alert FinanceErrorAlert = new Alert(Alert.AlertType.ERROR);

            FinanceErrorAlert.setHeaderText("Connection Error");
            FinanceErrorAlert.setContentText("Error connecting the server");
            FinanceErrorAlert.showAndWait();
        }



        //RegEx to extract the ratio from the ConvertedAmount which contains the entire webpage
        Pattern RegExPattern = Pattern.compile("[0-9^.]+"); //Match any numbers ranging from 0 to 9 and decimal point
        Matcher MatcherForPattern = RegExPattern.matcher(ConvertedAmount);

        while(MatcherForPattern.find())
        {
            System.out.println(MatcherForPattern.group()); //printing for reference
            FinalAmount = Float.valueOf(MatcherForPattern.group());
        }
        CurrencyToLabel.setText(String.valueOf(Float.valueOf(CurrencyFromText.getText()) * FinalAmount));

    }

    @FXML
    void close(ActionEvent e) {
        Platform.exit();
    }

    @FXML
    void About(ActionEvent e)
    {
        Alert AboutField = new Alert(Alert.AlertType.INFORMATION);
        AboutField.setHeaderText("Team");
        AboutField.setContentText("Ajay Nair \n Parth Nair\n Gokul Chathankulam");
        AboutField.showAndWait();
    }

    @FXML
    void GitHub(ActionEvent e)
    {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process BrowserProcess = runtime.exec("firefox https://github.com/REKTR3X99/PBL_SEM3_BETTERGUI");
        }catch(Exception ProcessException)
        {
            ProcessException.printStackTrace();
        }
    }

}
