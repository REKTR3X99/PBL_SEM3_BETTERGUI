package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {


    private ObservableList<String> CountryList;
    private ObservableList<String> CapitalList;
    private ObservableList<String> GMTOffset;


    @FXML
     private ComboBox CountryComboBox;

    @FXML
    private Label CapitalDisplayLabel;




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

    }

}
