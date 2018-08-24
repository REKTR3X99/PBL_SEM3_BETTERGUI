package Layout;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddController {

    @FXML
    private TextField CountryNameTextField;

    @FXML
    private TextField GMTOffsetTextField;

    @FXML
    private Button AddButton;

    @FXML
    private Button CancelButton;


    @FXML
    void AddItemToList(ActionEvent e)
    {
        String Country;
        String GMTOffset;

        if(CountryNameTextField.getText().isEmpty()
                || GMTOffsetTextField.getText().isEmpty()
                || !GMTOffsetTextField.getText().startsWith("+")
                || !GMTOffsetTextField.getText().startsWith("-"))
        {
            Alert FieldEmptyAlert = new Alert(Alert.AlertType.ERROR);
            FieldEmptyAlert.setHeaderText("Error");
            FieldEmptyAlert.setContentText("One of the required field(s) is empty or the " +
                    "Syntax is wrong");

            FieldEmptyAlert.showAndWait();
        }else
        {
            Country = CountryNameTextField.getText();
            GMTOffset = GMTOffsetTextField.getText();
        }

    }


}
