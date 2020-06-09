package plantenApp.java.model.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class ComboBoxData extends SearchBase {
    private StringProperty value = new SimpleStringProperty("");

    public void Bind(CheckBox cbDoSearch, ComboBox<String> comboBox) {
        try {
            doSearchProperty().bind(cbDoSearch.selectedProperty());
            comboBox.disableProperty().bind(cbDoSearch.selectedProperty().not());
            valueProperty().bind(comboBox.valueProperty());
        } catch (NullPointerException nex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Checkbox: ").append(cbDoSearch).append(" combobox: ").append(comboBox).append('\n').append(nex.getMessage());
            System.out.println(sb.toString());
        }
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
