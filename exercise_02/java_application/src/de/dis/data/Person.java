package de.dis.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Person {
    private int person_id = -1;
    private String first_name;
    private String last_name;
    private String address;

    public int getPersonId() {
        return person_id;
    }

    public void setPersonId(int person_id) {
        this.person_id = person_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Person load(int person_id) {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String selectSQL = "SELECT * FROM person WHERE person_id = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, person_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Person p = new Person();
                p.setPersonId(rs.getInt("person_id"));
                p.setFirstName(rs.getString("first_name"));
                p.setLastName(rs.getString("last_name"));
                p.setAddress(rs.getString("address"));
                rs.close();
                pstmt.close();
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            if (getPersonId() == -1) {
                String insertSQL = "INSERT INTO person (first_name, last_name, address) VALUES (?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, getFirstName());
                pstmt.setString(2, getLastName());
                pstmt.setString(3, getAddress());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    setPersonId(rs.getInt(1));
                }
                rs.close();
                pstmt.close();
            } else {
                String updateSQL = "UPDATE person SET first_name = ?, last_name = ?, address = ? WHERE person_id = ?";
                PreparedStatement pstmt = con.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, getFirstName());
                pstmt.setString(2, getLastName());
                pstmt.setString(3, getAddress());
                pstmt.setInt(4, getPersonId());
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        Connection con = DbConnectionManager.getInstance().getConnection();
        try {
            if (getPersonId() == -1) {
                // print error message
                System.out.println("Person existiert nicht (mehr) in der Datenbank.");
                return;
            }
            String deleteSQL = "DELETE FROM person WHERE person_id = ?";
            PreparedStatement pstmt = con.prepareStatement(deleteSQL);
            pstmt.setInt(1, getPersonId());
            pstmt.executeUpdate();
            pstmt.close();
            setPersonId(-1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
