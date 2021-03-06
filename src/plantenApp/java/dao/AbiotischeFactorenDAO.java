package plantenApp.java.dao;

import plantenApp.java.model.*;
import plantenApp.java.model.data.*;
import plantenApp.java.model.data.enums.EComCheckbox;
import plantenApp.java.model.data.enums.EComboBox;
import plantenApp.java.model.data.enums.ESliderLabel;
import plantenApp.java.model.data.enums.ESpinner;
import plantenApp.java.utils.DaoUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Siebe
 */
public class AbiotischeFactorenDAO implements Queries {
    private Connection dbConnection;
    private PreparedStatement stmtSelectAbioByID;
    private PreparedStatement stmtSelectAbioMultiByID;

    public AbiotischeFactorenDAO(Connection dbConnection) throws SQLException {
        this.dbConnection = dbConnection;

        stmtSelectAbioByID = dbConnection.prepareStatement(GETABIOTISCHBYPLANTID);
        stmtSelectAbioMultiByID = dbConnection.prepareStatement(GETABIOTISCHBMULTIYPLANTID);
    }

    //region GET

    /**
     * @param id -> plant_id
     * @return -> alle abiotische factoren van de specifieke plant
     * @author Siebe
     */
    public AbiotischeFactoren getById(int id) throws SQLException {
        //Dao

        //Items
        AbiotischeFactoren abio = null;

        //SqlCommand
        stmtSelectAbioByID.setInt(1, id);
        ResultSet rs = stmtSelectAbioByID.executeQuery();
        if (rs.next()) {
            abio = new AbiotischeFactoren(
                    rs.getInt("abiotische_id"),
                    rs.getInt("plant_id"),
                    rs.getString("bezonning"),
                    rs.getString("grondsoort"),
                    rs.getString("vochtbehoefte"),
                    rs.getString("voedingsbehoefte"),
                    rs.getString("reactie_antagonistische_omg"),
                    getByIdMulti(id)
            );
        }

        //Output
        return abio;
    }

    /**
     * @param id -> plant_id
     * @return -> alle abiotische_multi factoren van de specifieke plant
     * @author Siebe
     * word alleen gebruikt in getById
     */
    private ArrayList<AbioMulti_Eigenschap> getByIdMulti(int id) throws SQLException {
        //Dao

        //Items
        ArrayList<AbioMulti_Eigenschap> abioMulti = new ArrayList<>();

        //SqlCommand
        stmtSelectAbioMultiByID.setInt(1, id);
        ResultSet rs = stmtSelectAbioMultiByID.executeQuery();
        while (rs.next()) {
            AbioMulti_Eigenschap abioEigenschap = new AbioMulti_Eigenschap(
                    rs.getInt("abiotische_id"),
                    rs.getString("eigenschap"),
                    rs.getString("waarde")
            );
            abioMulti.add(abioEigenschap);
        }

        //Output
        return abioMulti;
    }

    //endregion

    //region FILTER

    /**
     * @author Siebe
     * @param plantIds -> The ids that need to be filtered
     * @return The filtered ids
     */
    public ArrayList<Integer> FilterOn(ArrayList<Integer> plantIds, GUIdata guiData) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<>();

        SliderLabelData bezonning = guiData.sliderLabelDEM.get(ESliderLabel.BEZONNING);
        CombinedCheckboxData grondsoort = guiData.combinedCheckboxDEM.get(EComCheckbox.GRONDSOORT);
        SliderLabelData vochtbehoefte = guiData.sliderLabelDEM.get(ESliderLabel.VOCHTBEHOEFTE);
        SliderLabelData voedingsbehoefte = guiData.sliderLabelDEM.get(ESliderLabel.VOEDINGSBEHOEFTE);
        ComboBoxData reactieantaomgeving = guiData.comboBoxDEM.get(EComboBox.REACTIEANTAGONISTISCHEOMGEVING);

        //Nieuwe querybuilder voor de standaard eigenschappen
        QueryBuilder QB = new QueryBuilder("plant_id", "abiotische_factoren");

        QB.AddIN("plant_id",plantIds);

        if (bezonning.isDoSearch()) QB.AddBasicString("bezonning", bezonning.getActualValue());
        if (grondsoort.isDoSearch()) QB.AddBasicString("grondsoort", grondsoort.getActualValue());
        if (vochtbehoefte.isDoSearch()) QB.AddBasicString("vochtbehoefte", vochtbehoefte.getActualValue());
        if (voedingsbehoefte.isDoSearch()) QB.AddBasicString("voedingsbehoefte", voedingsbehoefte.getActualValue());
        if (reactieantaomgeving.isDoSearch()) QB.AddBasicString("reactie_antagonistische_omg", reactieantaomgeving.getValue());

        System.out.println(QB.getQuery());

        ResultSet rs = QB.PrepareStatement(dbConnection).executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("plant_id"));
        }

        //Indien op habitat gezocht word en het resultaat van de standaard filter niet leeg filter op habitat
        ComboBoxData habitat = guiData.comboBoxDEM.get(EComboBox.HABITAT);
        if (habitat.isDoSearch() && ids.size() > 0) {
            ArrayList<Integer> localIds = new ArrayList<>();
            QueryBuilder QBM = new QueryBuilder("plant_id", "abiotisch_multi");

            QBM.AddIN("plant_id", plantIds);

            QBM.AddBasicString("eigenschap", "Habitat");
            QBM.AddBasicString("waarde", habitat.getValue());

            System.out.println(QBM.getQuery());

            rs = QBM.PrepareStatement(dbConnection).executeQuery();
            while (rs.next()) {
                localIds.add(rs.getInt("plant_id"));
            }
            ids = localIds;
        }

        //Output
        return ids;
    }

    //endregion

}
