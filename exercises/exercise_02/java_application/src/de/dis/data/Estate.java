package de.dis.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Estate {
    private int estate_id = -1;
    private String city;
    private String postal_code;
    private String street;
    private String street_number;
    private int square_area;
    private int agent_id;

    public int getEstateId() {
        return estate_id;
    }

    public void setEstateId(int estate_id) {
        this.estate_id = estate_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postal_code;
    }

    public void setPostalCode(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return street_number;
    }

    public void setStreetNumber(String street_number) {
        this.street_number = street_number;
    }

    public int getSquareArea() {
        return square_area;
    }

    public void setSquareArea(int square_area) {
        this.square_area = square_area;
    }

    public int getAgentId() {
        return agent_id;
    }

    public void setAgentId(int agent_id) {
        this.agent_id = agent_id;
    }

    /**
     * Lädt ein Objekt aus der Datenbank
     *
     * @param id ID des zu ladenden Objekts
     * @return Objekt-Instanz
     */
    public static Estate load(int id) {
        try {
            Connection conn = DbConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM estate WHERE estate_id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Estate estate = new Estate();
                estate.setEstateId(rs.getInt("estate_id"));
                estate.setCity(rs.getString("city"));
                estate.setPostalCode(rs.getString("postal_code"));
                estate.setStreet(rs.getString("street"));
                estate.setStreetNumber(rs.getString("street_number"));
                estate.setSquareArea(rs.getInt("square_area"));
                estate.setAgentId(rs.getInt("agent_id"));
                return estate;
            }
        } catch (SQLException e) {
            // ignore warning
            e.printStackTrace();
        }
        return null;
    }
}