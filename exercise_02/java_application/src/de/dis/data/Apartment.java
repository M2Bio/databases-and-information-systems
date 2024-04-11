package de.dis.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Apartment extends Estate {
    private int floor;
    private float rent;
    private int rooms;
    private boolean balcony;
    private boolean built_in_kitchen;
    private int renter_id;

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getRent() {
        return rent;
    }

    public void setRent(float rent) {
        this.rent = rent;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public boolean getBalcony() {
        return balcony;
    }

    public void setBalcony(int balcony) {
        if (balcony == 1) {
            this.balcony = true;
        } else if (balcony == 0) {
            this.balcony = false;
        } else {
            throw new IllegalArgumentException("Invalid value for balcony");
        }
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }

    public boolean getBuiltInKitchen() {
        return built_in_kitchen;
    }

    public void setBuiltInKitchen(int built_in_kitchen) {
        if (built_in_kitchen == 1) {
            this.built_in_kitchen = true;
        } else if (built_in_kitchen == 0) {
            this.built_in_kitchen = false;
        } else {
            throw new IllegalArgumentException("Invalid value for built_in_kitchen");
        }
    }

    public void setBuiltInKitchen(boolean built_in_kitchen) {
        this.built_in_kitchen = built_in_kitchen;
    }

    public int getRenterId() {
        return renter_id;
    }

    public void setRenterId(int renter_id) {
        this.renter_id = renter_id;
    }

    public static Apartment load(int estate_id) {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String query = "SELECT * FROM estate NATURAL JOIN apartment WHERE estate_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, estate_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Apartment a = new Apartment();
                a.setEstateId(rs.getInt("estate_id"));
                a.setCity(rs.getString("city"));
                a.setPostalCode(rs.getString("postal_code"));
                a.setStreet(rs.getString("street"));
                a.setStreetNumber(rs.getString("street_number"));
                a.setSquareArea(rs.getInt("square_area"));
                a.setAgentId(rs.getInt("agent_id"));
                a.setFloor(rs.getInt("floor"));
                a.setRent(rs.getFloat("rent"));
                a.setRooms(rs.getInt("rooms"));
                a.setBalcony(rs.getBoolean("balcony"));
                a.setBuiltInKitchen(rs.getBoolean("built_in_kitchen"));
                a.setRenterId(rs.getInt("renter_id"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            String query = "INSERT INTO estate (city, postal_code, street, street_number, square_area, agent_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, getCity());
            pstmt.setString(2, getPostalCode());
            pstmt.setString(3, getStreet());
            pstmt.setString(4, getStreetNumber());
            pstmt.setInt(5, getSquareArea());
            pstmt.setInt(6, getAgentId());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            setEstateId(rs.getInt(1));
            query = "INSERT INTO apartment (estate_id, floor, rent, rooms, balcony, built_in_kitchen) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getEstateId());
            pstmt.setInt(2, getFloor());
            pstmt.setFloat(3, getRent());
            pstmt.setInt(4, getRooms());
            pstmt.setBoolean(5, getBalcony());
            pstmt.setBoolean(6, getBuiltInKitchen());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            String query = "UPDATE estate SET city = ?, postal_code = ?, street = ?, street_number = ?, square_area = ?, agent_id = ? WHERE estate_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, getCity());
            pstmt.setString(2, getPostalCode());
            pstmt.setString(3, getStreet());
            pstmt.setString(4, getStreetNumber());
            pstmt.setInt(5, getSquareArea());
            pstmt.setInt(6, getAgentId());
            pstmt.setInt(7, getEstateId());
            pstmt.executeUpdate();
            query = "UPDATE apartment SET floor = ?, rent = ?, rooms = ?, balcony = ?, built_in_kitchen = ? WHERE estate_id = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getFloor());
            pstmt.setFloat(2, getRent());
            pstmt.setInt(3, getRooms());
            pstmt.setBoolean(4, getBalcony());
            pstmt.setBoolean(5, getBuiltInKitchen());
            pstmt.setInt(6, getEstateId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            String query = "DELETE FROM apartment WHERE estate_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getEstateId());
            pstmt.executeUpdate();
            query = "DELETE FROM estate WHERE estate_id = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getEstateId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
