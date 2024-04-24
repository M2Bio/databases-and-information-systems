package de.dis.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class House extends Estate {
    private int floors;
    private int price;
    private boolean garden_area;
    private int owner_id;

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getGardenArea() {
        return garden_area;
    }

    public void setGardenArea(int garden_area) {
        if (garden_area == 1) {
            this.garden_area = true;
        } else if (garden_area == 0) {
            this.garden_area = false;
        } else {
            throw new IllegalArgumentException("Invalid value for garden_area");
        }
    }

    public void setGardenArea(boolean garden_area) {
        this.garden_area = garden_area;
    }

    public int getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(int owner_id) {
        this.owner_id = owner_id;
    }

    public static House load(int estate_id) {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String query = "SELECT * FROM estate NATURAL JOIN house WHERE estate_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, estate_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                House house = new House();
                house.setEstateId(rs.getInt("estate_id"));
                house.setCity(rs.getString("city"));
                house.setPostalCode(rs.getString("postal_code"));
                house.setStreet(rs.getString("street"));
                house.setStreetNumber(rs.getString("street_number"));
                house.setSquareArea(rs.getInt("square_area"));
                house.setOwnerId(rs.getInt("agent_id"));
                house.setFloors(rs.getInt("floors"));
                house.setPrice(rs.getInt("price"));
                house.setGardenArea(rs.getBoolean("garden"));
                return house;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        Connection con = DbConnectionManager.getInstance().getConnection(); //.setAutoCommit(false)
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
            System.out.println(rs);
            if (rs.next()) {
                setEstateId(rs.getInt(1));
            }
            query = "INSERT INTO house (estate_id, floors, price, garden) VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getEstateId());
            pstmt.setInt(2, getFloors());
            pstmt.setInt(3, getPrice());
            pstmt.setBoolean(4, getGardenArea());
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
            query = "UPDATE house SET floors = ?, price = ?, garden = ? WHERE estate_id = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getFloors());
            pstmt.setInt(2, getPrice());
            pstmt.setBoolean(3, getGardenArea());
            pstmt.setInt(4, getEstateId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        //  TODO: on delete cascade
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            String query = "DELETE FROM estate WHERE estate_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, getEstateId());
            pstmt.executeUpdate();
            String query2 = "DELETE FROM house WHERE estate_id = ?";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, getEstateId());
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
