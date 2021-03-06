package plantenApp.java.dao;

import plantenApp.java.model.Plant;
import plantenApp.java.model.data.ComboBoxData;
import plantenApp.java.model.data.GUIdata;
import plantenApp.java.model.data.TextfieldData;
import plantenApp.java.model.data.enums.EComboBox;
import plantenApp.java.model.data.enums.ETextfield;
import plantenApp.java.utils.DaoUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Siebe
 */
public class PlantDAO implements Queries {

    private Connection dbConnection;
    private PreparedStatement stmtSelectById;

    public PlantDAO(Connection dbConnection) throws SQLException {
        this.dbConnection = dbConnection;

        stmtSelectById = dbConnection.prepareStatement(GETPLANTBYID);
    }

    //region GET

    /**
     * @param plantIds -> plant_ids
     * @return -> alleen de basis gegevens van een plant
     * @author Siebe
     */
    public ArrayList<Plant> GetPlantList(ArrayList<Integer> plantIds) throws SQLException {
        //Dao

        //Items
        ArrayList<Plant> plants = new ArrayList<>();

        //SQLcommand
        PreparedStatement stmtSelectByIds = DaoUtils.ReadyStatement(dbConnection, GETPLANTSBYIDS, plantIds);

        //SqlCommand
        ResultSet rs = stmtSelectByIds.executeQuery();
        while (rs.next()) {
            plants.add(new Plant(
                    rs.getInt("plant_id"),
                    rs.getString("type"),
                    rs.getString("familie"),
                    rs.getString("geslacht"),
                    rs.getString("soort"),
                    rs.getString("variatie"),
                    rs.getInt("plantdichtheid_min"),
                    rs.getInt("plantdichtheid_max")
            ));
        }

        //Output
        return plants;
    }

    /**
     * @param id -> plant_id
     * @return -> alleen de basis gegevens van een plant
     * @author Siebe
     */
    public Plant getPlantById(int id) throws SQLException {
        //Dao

        //Items
        Plant plant = null;

        //SqlCommand
        stmtSelectById.setInt(1, id);
        ResultSet rs = stmtSelectById.executeQuery();
        if (rs.next()) {
            plant = new Plant(
                    rs.getInt("plant_id"),
                    rs.getString("planttype"),
                    rs.getString("familie"),
                    rs.getString("geslacht"),
                    rs.getString("soort"),
                    rs.getString("variatie"),
                    rs.getInt("plantdichtheid_min"),
                    rs.getInt("plantdichtheid_max")
            );
        }

        //Output
        return plant;
    }

    /**
     * @param id -> plant_id
     * @return -> alles van een Plant
     * @author Siebe
     */
    public Plant getFullPlantById(int id) throws SQLException {
        //Dao
        AbiotischeFactorenDAO abiotischeFactorenDAO = new AbiotischeFactorenDAO(dbConnection);
        BeheerDAO beheerDAO = new BeheerDAO(dbConnection);
        CommensalismeDAO commensalismeDAO = new CommensalismeDAO(dbConnection);
        FenotypeDAO fenotypeDAO = new FenotypeDAO(dbConnection);
        ExtraDAO extraDAO = new ExtraDAO(dbConnection);
        FotoDAO fotoDAO = new FotoDAO(dbConnection);

        //Items
        Plant plant = null;

        //SqlCommand
        stmtSelectById.setInt(1, id);
        ResultSet rs = stmtSelectById.executeQuery();
        if (rs.next()) {
            plant = new Plant(
                    rs.getInt("plant_id"),
                    rs.getString("planttype"),
                    rs.getString("familie"),
                    rs.getString("geslacht"),
                    rs.getString("soort"),
                    rs.getString("variatie"),
                    rs.getInt("plantdichtheid_min"),
                    rs.getInt("plantdichtheid_max"),


                    fotoDAO.getFotoById(id),
                    beheerDAO.getById(id),
                    abiotischeFactorenDAO.getById(id),
                    commensalismeDAO.getById(id),
                    fenotypeDAO.getById(id),
                    extraDAO.getExtraById(id)

            );
        }

        //Output
        return plant;
    }

    //endregion

    //region FILTER

    public ArrayList<Integer> FilterOn(GUIdata guiData) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<>();

        ComboBoxData type = guiData.comboBoxDEM.get(EComboBox.TYPE);
        ComboBoxData familie = guiData.comboBoxDEM.get(EComboBox.FAMILIE);
        ComboBoxData geslacht = guiData.comboBoxDEM.get(EComboBox.GESLACHT);
        ComboBoxData soort = guiData.comboBoxDEM.get(EComboBox.SOORT);
        ComboBoxData variant = guiData.comboBoxDEM.get(EComboBox.VARIANT);
        TextfieldData fgsv = guiData.textFieldDEM.get(ETextfield.SEARCH);

        QueryBuilder QB = new QueryBuilder("plant_id", "plant");

        if (type.isDoSearch()) QB.AddBasicString("planttype", type.getValue());
        if (familie.isDoSearch()) QB.AddBasicString("familie", familie.getValue());
        if (geslacht.isDoSearch()) QB.AddBasicString("geslacht", geslacht.getValue());
        if (soort.isDoSearch()) QB.AddBasicString("soort", soort.getValue());
        if (variant.isDoSearch()) QB.AddBasicString("variatie", soort.getValue());
        if (fgsv.isDoSearch()) QB.AddLIKEString("fgsv", "%" + fgsv.getValue() + "%");

        System.out.println(QB.getQuery());

        ResultSet rs = QB.PrepareStatement(dbConnection).executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("plant_id"));
        }


        /*
        //Dao

        //Items
        ArrayList<Integer> ids = new ArrayList<>();

        //type
        ComboBoxData type = guiData.comboBoxDEM.get(EComboBox.TYPE);
        stmtSelectIdsByPlant.setString(1, type.getValue());
        stmtSelectIdsByPlant.setInt(2, (type.isDoSearch()) ? 0 : 1);

        //familie
        ComboBoxData familie = guiData.comboBoxDEM.get(EComboBox.FAMILIE);
        stmtSelectIdsByPlant.setString(3, familie.getValue());
        stmtSelectIdsByPlant.setInt(4, (familie.isDoSearch()) ? 0 : 1);

        //geslacht
        ComboBoxData geslacht = guiData.comboBoxDEM.get(EComboBox.GESLACHT);
        stmtSelectIdsByPlant.setString(5, geslacht.getValue());
        stmtSelectIdsByPlant.setInt(6, (geslacht.isDoSearch()) ? 0 : 1);

        //soort
        ComboBoxData soort = guiData.comboBoxDEM.get(EComboBox.SOORT);
        stmtSelectIdsByPlant.setString(7, soort.getValue());
        stmtSelectIdsByPlant.setInt(8, (soort.isDoSearch()) ? 0 : 1);

        //variant
        ComboBoxData variant = guiData.comboBoxDEM.get(EComboBox.VARIANT);
        stmtSelectIdsByPlant.setString(9, variant.getValue());
        stmtSelectIdsByPlant.setInt(10, (variant.isDoSearch()) ? 0 : 1);

        //fgsv
        TextfieldData fgsv = guiData.textFieldDEM.get(ETextfield.SEARCH);
        stmtSelectIdsByPlant.setString(11, "%" + fgsv.getValue() + "%");
        stmtSelectIdsByPlant.setInt(12, (fgsv.isDoSearch()) ? 0 : 1);



        ResultSet rs = stmtSelectIdsByPlant.executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("plant_id"));
        }

         */

        //Output
        return ids;
    }

    //endregion
}
