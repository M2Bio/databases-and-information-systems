package de.dis.data;

import java.sql.Date;

public class Contract {
    private int contract_id = -1;
    private int contract_no;
    private Date date;
    private String place;

    public int getContractId() {
        return contract_id;
    }

    public void setContractId(int contract_id) {
        this.contract_id = contract_id;
    }

    public int getContractNo() {
        return contract_no;
    }

    public void setContractNo(int contract_no) {
        this.contract_no = contract_no;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
