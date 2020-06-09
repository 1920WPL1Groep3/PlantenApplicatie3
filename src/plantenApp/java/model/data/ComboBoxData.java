package plantenApp.java.model.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;

public class ComboBoxData extends SearchBase {
    private StringProperty value = new SimpleStringProperty("");
    private ArrayList<String> possibleValues;

    public void Bind(CheckBox cbDoSearch, ComboBox<String> comboBox) {
        try {
            updateComboBoxWithPossibleValues(comboBox);
            doSearchProperty().bind(cbDoSearch.selectedProperty());
            comboBox.disableProperty().bind(cbDoSearch.selectedProperty().not());
            valueProperty().bind(comboBox.valueProperty());
        } catch (NullPointerException nex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Checkbox: ").append(cbDoSearch).append(" combobox: ").append(comboBox).append('\n').append(nex.getMessage());
            System.out.println(sb.toString());
        }
    }

    public ArrayList<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(ArrayList<String> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void updateComboBoxWithPossibleValues(ComboBox<String> comboBox){
        comboBox.getItems().clear();
        comboBox.getItems().addAll(possibleValues);
        comboBox.getSelectionModel().selectFirst();
    }

    //StringValue
    public StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
