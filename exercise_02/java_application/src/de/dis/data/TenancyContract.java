package de.dis.data;

import java.sql.Date;
import java.sql.*;

public class TenancyContract extends Contract {
    private Date start_date;
    private int duration;
    private int additional_costs;
    private int apartment_id;
    private int renter_id;

    public Date getStartDate() {
        return start_date;
    }

    public void setStartDate(Date start_date) {
        this.start_date = start_date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAdditionalCosts() {
        return additional_costs;
    }

    public void setAdditionalCosts(int additional_costs) {
        this.additional_costs = additional_costs;
    }

    public int getApartmentId() {
        return apartment_id;
    }

    public void setApartmentId(int apartment_id) {
        this.apartment_id = apartment_id;
    }

    public int getRenterId() {
        return renter_id;
    }

    public void setRenterId(int person_id) {
        this.renter_id = person_id;
    }

    public static TenancyContract load(int contract_id) {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String selectSQL = "SELECT * FROM tenancy_contract WHERE contract_id = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, contract_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                TenancyContract tc = new TenancyContract();
                tc.setContractId(rs.getInt("contract_id"));
                tc.setContractNo(rs.getInt("contract_no"));
                tc.setDate(rs.getDate("date"));
                tc.setPlace(rs.getString("place"));
                tc.setStartDate(rs.getDate("start_date"));
                tc.setDuration(rs.getInt("duration"));
                tc.setAdditionalCosts(rs.getInt("additional_costs"));
                tc.setApartmentId(rs.getInt("apartment_id"));
                tc.setRenterId(rs.getInt("person_id"));
                rs.close();
                return tc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String insertSQL = "INSERT INTO contract (contract_no, date, place) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, getContractNo());
            pstmt.setDate(2, getDate());
            pstmt.setString(3, getPlace());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            setContractId(rs.getInt(1));
            rs.close();
            pstmt.close();

            String insertSQL2 = "INSERT INTO tenancy_contract (contract_id, start_date, duration, additional_costs, apartment_id, renter_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt2 = con.prepareStatement(insertSQL2);
            pstmt2.setInt(1, getContractId());
            pstmt2.setDate(2, getStartDate());
            pstmt2.setInt(3, getDuration());
            pstmt2.setInt(4, getAdditionalCosts());
            pstmt2.setInt(5, getApartmentId());
            pstmt2.setInt(6, getRenterId());
            pstmt2.executeUpdate();
            pstmt2.close();

            String insertSQL3 = "UPDATE apartment SET renter_id = ? WHERE estate_id = ?";
            PreparedStatement pstmt3 = con.prepareStatement(insertSQL3);
            pstmt3.setInt(1, getRenterId());
            pstmt3.setInt(2, getApartmentId());
            pstmt3.executeUpdate();
            pstmt3.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
