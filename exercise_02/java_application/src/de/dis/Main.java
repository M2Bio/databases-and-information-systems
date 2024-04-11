package de.dis;

import de.dis.data.*;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Hauptklasse
 */
public class Main {
    /**
     * Startet die Anwendung
     */
    public static void main(String[] args) {
        showMainMenu();
    }

    /**
     * Zeigt das Hauptmenü
     */
    public static void showMainMenu() {
        //Menüoptionen
        final int MENU_MAKLER = 0;
        final int MENU_IMMOBILIEN = 1;
        final int MENU_VERTRAG = 2;
        final int QUIT = 3;

        //Erzeuge Menü
        Menu mainMenu = new Menu("Hauptmenü");
        mainMenu.addEntry("Makler-Verwaltung", MENU_MAKLER);
        mainMenu.addEntry("Immobilien-Verwaltung", MENU_IMMOBILIEN);
        mainMenu.addEntry("Vertrags-Verwaltung", MENU_VERTRAG);
        mainMenu.addEntry("Beenden", QUIT);

        //Verarbeite Eingabe
        while (true) {
            int response = mainMenu.show();

            switch (response) {
                case MENU_MAKLER:
                    showMaklerMenu();
                    break;
                case MENU_IMMOBILIEN:
                    showEstateMenu();
                    break;
                case MENU_VERTRAG:
                    showContractMenu();
                    break;
                case QUIT:
                    return;
            }
        }
    }
    //-------------------------------------------------------------------------------------------

    /**
     * Zeigt die Maklerverwaltung
     */
    public static void showMaklerMenu() {
        //Menüoptionen
        final int NEW_MAKLER = 0;
        final int EDIT_MAKLER = 1;
        final int BACK = 2;

        //Maklerverwaltungsmenü
        Menu maklerMenu = new Menu("Makler-Verwaltung");
        maklerMenu.addEntry("Neuer Makler", NEW_MAKLER);
        maklerMenu.addEntry("Makler bearbeiten", EDIT_MAKLER);
        maklerMenu.addEntry("Zurück zum Hauptmenü", BACK);

        String password = FormUtil.readString("Admin-Passwort");
        if (!password.equals("admin")) {
            System.out.println("Falsches Passwort.");
            return;
        }
        //Verarbeite Eingabe
        while (true) {
            int response = maklerMenu.show();

            switch (response) {
                case NEW_MAKLER:
                    newMakler();
                    break;
                case EDIT_MAKLER:
                    editMakler();
                    break;
                case BACK:
                    return;
            }
        }
    }

    /**
     * Legt einen neuen Makler an, nachdem der Benutzer
     * die entprechenden Daten eingegeben hat.
     */
    public static void newMakler() {
        Makler m = new Makler();

        m.setName(FormUtil.readString("Name"));
        m.setAddress(FormUtil.readString("Adresse"));
        m.setLogin(FormUtil.readString("Login"));
        m.setPassword(FormUtil.readString("Passwort"));
        m.save();

        System.out.println("Makler mit der ID " + m.getId() + " wurde erzeugt.");
    }

    public static void editMakler() {
        final int UPDATE_MAKLER = 0;
        final int DELETE_MAKLER = 1;
        final int BACK = 2;
        Menu editMaklerMenu = new Menu("Makler bearbeiten");
        editMaklerMenu.addEntry("Maklerdaten bearbeiten", UPDATE_MAKLER);
        editMaklerMenu.addEntry("Makler löschen", DELETE_MAKLER);
        editMaklerMenu.addEntry("Zurück", BACK);
        int id = FormUtil.readInt("ID des zu bearbeitenden Maklers");
        Makler m = Makler.load(id);

        if (m == null) {
            System.out.println("Makler mit der ID " + id + " existiert nicht.");
            return;
        }

        while (true) {
            int response = editMaklerMenu.show();

            switch (response) {
                case UPDATE_MAKLER:
                    m.setName(FormUtil.readString("Name (" + m.getName() + ")"));
                    m.setAddress(FormUtil.readString("Adresse (" + m.getAddress() + ")"));
                    m.setLogin(FormUtil.readString("Login (" + m.getLogin() + ")"));
                    m.setPassword(FormUtil.readString("Passwort (" + m.getPassword() + ")"));
                    m.save();
                    System.out.println("Makler mit der ID " + m.getId() + " wurde bearbeitet.");
                    break;
                case DELETE_MAKLER:
                    String confirm = FormUtil.readString("Makler (" + m.getName() + ") wirklich löschen? (j/n)");
                    if (!confirm.equals("j")) {
                        System.out.println("Makler wurde nicht gelöscht.");
                        break;
                    }
                    System.out.println("Makler mit der ID " + m.getId() + " wird gelöscht.");
                    m.delete();
                    break;
                case BACK:
                    return;
            }
        }
    }

    //-------------------------------------------------------------------------------------------
    public static void showEstateMenu() {
        //Menüoptionen
        final int NEW_ESTATE = 0;
        final int EDIT_ESTATE = 1;
        final int DELETE_ESTATE = 2;
        final int BACK = 3;

        //Immobilienverwaltungsmenü
        Menu estateMenu = new Menu("Immobilien-Verwaltung");
        estateMenu.addEntry("Neues Immobile anlegen", NEW_ESTATE);
        estateMenu.addEntry("Immobilie bearbeiten", EDIT_ESTATE);
        estateMenu.addEntry("Immobile löschen", DELETE_ESTATE);
        estateMenu.addEntry("Zurück zum Hauptmenü", BACK);
        String username = FormUtil.readString("Benutzername");
        String password = FormUtil.readString("Passwort");
        Makler login = Makler.login(username, password);
        if (login == null) {
            System.out.println("Falscher Benutzername oder Passwort.");
            return;
        }
        System.out.println("Willkommen " + login.getName() + "!");
        //Verarbeite Eingabe
        while (true) {
            int response = estateMenu.show();

            switch (response) {
                case NEW_ESTATE:
                    newEstate();
                    break;
                case EDIT_ESTATE:
                    editEstate();
                    break;
                case DELETE_ESTATE:
                    deleteEstate();
                    break;
                case BACK:
                    return;
            }
        }
    }

    public static void newEstate() {
        final int HOUSE = 0;
        final int APARTMENT = 1;
        final int BACK = 2;
        Menu newEstateMenu = new Menu("Neue Immobilie anlegen");
        newEstateMenu.addEntry("Haus", HOUSE);
        newEstateMenu.addEntry("Wohnung", APARTMENT);
        newEstateMenu.addEntry("Zurück", BACK);

        while (true) {
            int response = newEstateMenu.show();
            switch (response) {
                case HOUSE:
                    newHouse();
                    break;
                case APARTMENT:
                    newApartment();
                    break;
                case BACK:
                    return;
            }
        }
    }

    public static void newHouse() {
        System.out.println("Neues Haus anlegen");

        House h = new House();
        h.setCity(FormUtil.readString("Stadt"));
        h.setPostalCode(FormUtil.readString("Postleitzahl"));
        h.setStreet(FormUtil.readString("Straße"));
        h.setStreetNumber(FormUtil.readString("Hausnummer"));
        h.setSquareArea(FormUtil.readInt("Wohnfläche"));
        h.setFloors(FormUtil.readInt("Anzahl Etagen"));
        h.setPrice(FormUtil.readInt("Preis"));
        h.setGardenArea(FormUtil.readInt("Gartenfläche (1 falls vorhanden, 0 falls nicht)"));
        h.setAgentId(FormUtil.readInt("Makler-ID"));
        h.save();
    }

    public static void newApartment() {
        System.out.println("Neue Wohnung anlegen");

        Apartment a = new Apartment();
        a.setCity(FormUtil.readString("Stadt"));
        a.setPostalCode(FormUtil.readString("Postleitzahl"));
        a.setStreet(FormUtil.readString("Straße"));
        a.setStreetNumber(FormUtil.readString("Hausnummer"));
        a.setSquareArea(FormUtil.readInt("Wohnfläche"));
        a.setFloor(FormUtil.readInt("Etage"));
        a.setRent(FormUtil.readInt("Miete"));
        a.setRooms(FormUtil.readInt("Zimmer"));
        a.setBalcony(FormUtil.readInt("Balkon (1 falls vorhanden, 0 falls nicht)"));
        a.setBuiltInKitchen(FormUtil.readInt("Einbauküche (1 falls vorhanden, 0 falls nicht)"));
        a.setAgentId(FormUtil.readInt("Makler-ID"));
        a.save();
    }

    public static void editEstate() {
        final int UPDATE_HOUSE = 0;
        final int UPDATE_APARTMENT = 1;
        final int BACK = 2;
        Menu editEstateMenu = new Menu("Immobilie bearbeiten");
        editEstateMenu.addEntry("Haus bearbeiten", UPDATE_HOUSE);
        editEstateMenu.addEntry("Wohnung bearbeiten", UPDATE_APARTMENT);
        editEstateMenu.addEntry("Zurück", BACK);

        while (true) {
            int response = editEstateMenu.show();
            switch (response) {
                case UPDATE_HOUSE:
                    int estate_id = FormUtil.readInt("ID des zu bearbeitenden Hauses");
                    House h = House.load(estate_id);
                    if (h == null) {
                        System.out.println("Haus mit der ID " + estate_id + " existiert nicht.");
                        break;
                    }
                    h.setCity(FormUtil.readString("Stadt (" + h.getCity() + ")"));
                    h.setPostalCode(FormUtil.readString("Postleitzahl (" + h.getPostalCode() + ")"));
                    h.setStreet(FormUtil.readString("Straße (" + h.getStreet() + ")"));
                    h.setStreetNumber(FormUtil.readString("Hausnummer (" + h.getStreetNumber() + ")"));
                    h.setSquareArea(FormUtil.readInt("Wohnfläche (" + h.getSquareArea() + ")"));
                    h.setFloors(FormUtil.readInt("Anzahl Etagen (" + h.getFloors() + ")"));
                    h.setPrice(FormUtil.readInt("Preis (" + h.getPrice() + ")"));
                    h.setGardenArea(FormUtil.readInt("Gartenfläche (1 falls vorhanden, 0 falls nicht)"));
                    h.setAgentId(FormUtil.readInt("Makler-ID (" + h.getAgentId() + ")"));
                    h.update();
                    System.out.println("Haus mit der ID " + h.getEstateId() + " wurde bearbeitet.");
                    break;
                case UPDATE_APARTMENT:
                    int estate_id2 = FormUtil.readInt("ID der zu bearbeitenden Wohnung");
                    Apartment a = Apartment.load(estate_id2);
                    if (a == null) {
                        System.out.println("Wohnung mit der ID " + estate_id2 + " existiert nicht.");
                        break;
                    }
                    a.setCity(FormUtil.readString("Stadt (" + a.getCity() + ")"));
                    a.setPostalCode(FormUtil.readString("Postleitzahl (" + a.getPostalCode() + ")"));
                    a.setStreet(FormUtil.readString("Straße (" + a.getStreet() + ")"));
                    a.setStreetNumber(FormUtil.readString("Hausnummer (" + a.getStreetNumber() + ")"));
                    a.setSquareArea(FormUtil.readInt("Wohnfläche (" + a.getSquareArea() + ")"));
                    a.setFloor(FormUtil.readInt("Etage (" + a.getFloor() + ")"));
                    a.setRent(FormUtil.readInt("Miete (" + a.getRent() + ")"));
                    a.setRooms(FormUtil.readInt("Zimmer (" + a.getRooms() + ")"));
                    a.setBalcony(FormUtil.readInt("Balkon (1 falls vorhanden, 0 falls nicht)"));
                    a.setBuiltInKitchen(FormUtil.readInt("Einbauküche (1 falls vorhanden, 0 falls nicht)"));
                    a.setAgentId(FormUtil.readInt("Makler-ID (" + a.getAgentId() + ")"));
                    a.update();
                    System.out.println("Wohnung mit der ID " + a.getEstateId() + " wurde bearbeitet.");
                    break;
                case BACK:
                    return;
            }
        }
    }

    public static void deleteEstate() {
        final int DELETE_HOUSE = 0;
        final int DELETE_APARTMENT = 1;
        final int BACK = 2;
        Menu deleteEstateMenu = new Menu("Immobilie löschen");
        deleteEstateMenu.addEntry("Haus löschen", DELETE_HOUSE);
        deleteEstateMenu.addEntry("Wohnung löschen", DELETE_APARTMENT);
        deleteEstateMenu.addEntry("Zurück", BACK);

        while (true) {
            int response = deleteEstateMenu.show();
            switch (response) {
                case DELETE_HOUSE:
                    int estate_id = FormUtil.readInt("ID des zu löschenden Hauses");
                    House h = House.load(estate_id);
                    if (h == null) {
                        System.out.println("Haus mit der ID " + estate_id + " existiert nicht.");
                        break;
                    }
                    String confirm = FormUtil.readString("Haus (" + h.getEstateId() + ") wirklich löschen? (j/n)");
                    if (!confirm.equals("j")) {
                        System.out.println("Haus wurde nicht gelöscht.");
                        break;
                    }
                    System.out.println("Haus mit der ID " + h.getEstateId() + " wird gelöscht.");
                    h.delete();
                    break;
                case DELETE_APARTMENT:
                    int estate_id2 = FormUtil.readInt("ID der zu löschenden Wohnung");
                    Apartment a = Apartment.load(estate_id2);
                    if (a == null) {
                        System.out.println("Wohnung mit der ID " + estate_id2 + " existiert nicht.");
                        break;
                    }
                    String confirm2 = FormUtil.readString("Wohnung (" + a.getEstateId() + ") wirklich löschen? (j/n)");
                    if (!confirm2.equals("j")) {
                        System.out.println("Wohnung wurde nicht gelöscht.");
                        break;
                    }
                    System.out.println("Wohnung mit der ID " + a.getEstateId() + " wird gelöscht.");
                    a.delete();
                    break;
                case BACK:
                    return;
            }
        }
    }

    //-------------------------------------------------------------------------------------------
    public static void showContractMenu() {
        //Menüoptionen
        final int NEW_PERSON = 0;
        final int NEW_CONTRACT = 1;
        final int SHOW_CONTRACTS = 2;
        final int BACK = 3;

        //Vertragsverwaltungsmenü
        Menu contractMenu = new Menu("Vertrags-Verwaltung");
        contractMenu.addEntry("Neue Person", NEW_PERSON);
        contractMenu.addEntry("Neuer Vertrag", NEW_CONTRACT);
        contractMenu.addEntry("Verträge anzeigen", SHOW_CONTRACTS);
        contractMenu.addEntry("Zurück zum Hauptmenü", BACK);

        //Verarbeite Eingabe
        while (true) {
            int response = contractMenu.show();

            switch (response) {
                case NEW_PERSON:
                    newPerson();
                    break;
                case NEW_CONTRACT:
                    newContract();
                    break;
                case SHOW_CONTRACTS:
                    showContracts();
                    break;
                case BACK:
                    return;
            }
        }
    }

    public static void newPerson() {
        System.out.println("Neue Person anlegen");

        Person p = new Person();
        p.setFirstName(FormUtil.readString("Vorname"));
        p.setLastName(FormUtil.readString("Nachname"));
        p.setAddress(FormUtil.readString("Adresse"));
        p.save();
    }

    public static void newContract() {
        final int PURCHASE_CONTRACT = 0;
        final int TENANCY_CONTRACT = 1;
        final int BACK = 2;
        Menu newContractMenu = new Menu("Neuen Vertrag anlegen");
        newContractMenu.addEntry("Kaufvertrag", PURCHASE_CONTRACT);
        newContractMenu.addEntry("Mietvertrag", TENANCY_CONTRACT);
        newContractMenu.addEntry("Zurück", BACK);

        while (true) {
            int response = newContractMenu.show();
            switch (response) {
                case PURCHASE_CONTRACT:
                    newPurchaseContract();
                    break;
                case TENANCY_CONTRACT:
                    newTenancyContract();
                    break;
                case BACK:
                    return;
            }
        }
    }

    public static void newPurchaseContract() {
        System.out.println("Neuen Kaufvertrag anlegen");

        PurchaseContract p = new PurchaseContract();
        p.setContractNo(FormUtil.readInt("Vertragsnummer"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = FormUtil.readString("Datum (yyyy-MM-dd)");
        java.util.Date parsedDate;
        try {
            parsedDate = sdf.parse(date);
        } catch (Exception e) {
            System.out.println("Ungültiges Datum.");
            return;
        }
        p.setDate(new Date(parsedDate.getTime()));
        p.setPlace(FormUtil.readString("Ort"));
        p.setNoOfInstallments(FormUtil.readInt("Anzahl Raten"));
        p.setInterestRate(FormUtil.readInt("Zinssatz"));
        p.setHouseId(FormUtil.readInt("Haus-ID"));
        p.setBuyerId(FormUtil.readInt("Käufer-ID"));
        p.save();
    }

    public static void newTenancyContract() {
        System.out.println("Neuen Mietvertrag anlegen");

        TenancyContract t = new TenancyContract();
        t.setContractNo(FormUtil.readInt("Vertragsnummer"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = FormUtil.readString("Datum (yyyy-MM-dd)");
        java.util.Date parsedDate;
        try {
            parsedDate = sdf.parse(date);
        } catch (Exception e) {
            System.out.println("Ungültiges Datum.");
            return;
        }
        t.setDate(new Date(parsedDate.getTime()));
        t.setPlace(FormUtil.readString("Ort"));
        t.setStartDate(new Date(parsedDate.getTime()));
        t.setDuration(FormUtil.readInt("Dauer"));
        t.setAdditionalCosts(FormUtil.readInt("Nebenkosten"));
        t.setApartmentId(FormUtil.readInt("Wohnung-ID"));
        t.setRenterId(FormUtil.readInt("Renter-ID"));
        t.save();
    }

    public static void showContracts() {
        System.out.println("Verträge anzeigen");

        String sql_purchase_contract = "SELECT * FROM contract NATURAL JOIN purchase_contract;";
        String sql_tenancy_contract = "SELECT * FROM contract NATURAL JOIN tenancy_contract;";
        try {
            Connection con = DbConnectionManager.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_purchase_contract);
            while (rs.next()) {
                System.out.println("Kaufvertrag");
                System.out.println("Vertragsnummer: " + rs.getInt("contract_no"));
                System.out.println("Datum: " + rs.getDate("date"));
                System.out.println("Ort: " + rs.getString("place"));
                System.out.println("Anzahl Raten: " + rs.getInt("no_of_installments"));
                System.out.println("Zinssatz: " + rs.getInt("interest_rate"));
                System.out.println("Haus-ID: " + rs.getInt("house_id"));
                System.out.println("Käufer-ID: " + rs.getInt("buyer_id"));
                System.out.println();
            }
            rs.close();

            rs = stmt.executeQuery(sql_tenancy_contract);
            while (rs.next()) {
                System.out.println("Mietvertrag");
                System.out.println("Vertragsnummer: " + rs.getInt("contract_no"));
                System.out.println("Datum: " + rs.getDate("date"));
                System.out.println("Ort: " + rs.getString("place"));
                System.out.println("Startdatum: " + rs.getDate("start_date"));
                System.out.println("Dauer: " + rs.getInt("duration"));
                System.out.println("Nebenkosten: " + rs.getInt("additional_costs"));
                System.out.println("Wohnung-ID: " + rs.getInt("apartment_id"));
                System.out.println("Renter-ID: " + rs.getInt("renter_id"));
                System.out.println();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}