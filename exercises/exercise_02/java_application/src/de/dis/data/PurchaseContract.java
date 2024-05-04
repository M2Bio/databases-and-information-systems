package de.dis.data;

import java.sql.Date;
import java.sql.*;

public class PurchaseContract extends Contract {
    private int no_of_installments;
    private int interest_rate;
    private int house_id;
    private int buyer_id;

    public int getNoOfInstallments() {
        return no_of_installments;
    }

    public void setNoOfInstallments(int no_of_installments) {
        this.no_of_installments = no_of_installments;
    }

    public int getInterestRate() {
        return interest_rate;
    }

    public void setInterestRate(int interest_rate) {
        this.interest_rate = interest_rate;
    }

    public int getHouseId() {
        return house_id;
    }

    public void setHouseId(int house_id) {
        this.house_id = house_id;
    }

    public int getBuyerId() {
        return buyer_id;
    }

    public void setBuyerId(int buyer_id) {
        this.buyer_id = buyer_id;
    }

    public static PurchaseContract load(int contract_id) {
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            String selectSQL = "SELECT * FROM purchase_contract WHERE contract_id = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, contract_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                PurchaseContract p = new PurchaseContract();
                p.setContractId(rs.getInt("contract_id"));
                p.setContractNo(rs.getInt("contract_no"));
                p.setDate(rs.getDate("date"));
                p.setPlace(rs.getString("place"));
                p.setNoOfInstallments(rs.getInt("no_of_installments"));
                p.setHouseId(rs.getInt("house_id"));
                p.setBuyerId(rs.getInt("buyer_id"));
                rs.close();
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
            if (getContractId() == -1) {
                String insertSQL = "INSERT INTO contract (contract_no, date, place) VALUES (?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, getContractNo());
                pstmt.setDate(2, getDate());
                pstmt.setString(3, getPlace());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    setContractId(rs.getInt(1));
                }
                rs.close();
                pstmt.close();

                String insertSQL2 = "INSERT INTO purchase_contract (contract_id, no_of_installments, interest_rate, house_id, buyer_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt2 = con.prepareStatement(insertSQL2);
                pstmt2.setInt(1, getContractId());
                pstmt2.setInt(2, getNoOfInstallments());
                pstmt2.setInt(3, getInterestRate());
                pstmt2.setInt(4, getHouseId());
                pstmt2.setInt(5, getBuyerId());
                pstmt2.executeUpdate();
                pstmt2.close();

                String updateSQL = "UPDATE house SET owner_id = ? WHERE estate_id = ?";
                PreparedStatement pstmt3 = con.prepareStatement(updateSQL);
                pstmt3.setInt(1, getBuyerId());
                pstmt3.setInt(2, getHouseId());
                pstmt3.executeUpdate();
                pstmt3.close();
            } else {
                String updateSQL = "UPDATE contract SET contract_no = ?, date = ?, place = ? WHERE contract_id = ?";
                PreparedStatement pstmt = con.prepareStatement(updateSQL);
                pstmt.setInt(1, getContractNo());
                pstmt.setDate(2, getDate());
                pstmt.setString(3, getPlace());
                pstmt.setInt(4, getContractId());
                pstmt.executeUpdate();
                pstmt.close();

                String updateSQL2 = "UPDATE purchase_contract SET no_of_installments = ?, interest_rate = ?, house_id = ?, buyer_id = ? WHERE contract_id = ?";
                PreparedStatement pstmt2 = con.prepareStatement(updateSQL2);
                pstmt2.setInt(1, getNoOfInstallments());
                pstmt2.setInt(2, getInterestRate());
                pstmt2.setInt(3, getHouseId());
                pstmt2.setInt(4, getBuyerId());
                pstmt2.setInt(5, getContractId());
                pstmt2.executeUpdate();
                pstmt2.close();

                String updateSQL3 = "UPDATE house SET owner_id = ? WHERE estate_id = ?";
                PreparedStatement pstmt3 = con.prepareStatement(updateSQL3);
                pstmt3.setInt(1, getBuyerId());
                pstmt3.setInt(2, getHouseId());
                pstmt3.executeUpdate();
                pstmt3.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
