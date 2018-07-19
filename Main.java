import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";

    private static void apartmentsDataBase() {
        try {
            String db_Connection = "jdbc:mysql://localhost:3306/mysqlhometask1?serverTimezone=UTC";
            Connection conn = DriverManager.getConnection(db_Connection, DB_USER, DB_PASSWORD);
            ApartmentsDBManager dbManager = new ApartmentsDBManager(conn);
            dbManager.createTableInDB();
            dbManager.putInitialDataToTable();
            dbManager.getDataFromDBByParameters(new String[]{"district='Podol'", "Prise>10000"});
            dbManager.getDataFromDBByParameters(new String[]{"squaremeters>100"});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ordersDataBase() {
        try {
            String db_Connection = "jdbc:mysql://localhost:3306/mysqlhometask2?serverTimezone=UTC";
            Connection conn = DriverManager.getConnection(db_Connection, DB_USER, DB_PASSWORD);
            OrdersDBManager dbManager = new OrdersDBManager(conn);
            dbManager.createTablesInDB();
            dbManager.putInitialDataToTables();

            dbManager.addNewGood("Mouse", 2020, 30);
            dbManager.addNewClient("Sashka", "Moon 24 str, house 4");
            dbManager.addNewOrderByID(3, 4);
            dbManager.addNewOrderByID(7, 8);
            dbManager.addNewOrderByID(7, 4);
            dbManager.addNewOrderByID(5, 6);
            dbManager.addNewOrderByName("Mouse", "Sashka");
            dbManager.addNewOrderByName("Star", "Sashka");

            dbManager.showTable("Clients");
            dbManager.showTable("Goods");
            dbManager.showTable("Orders");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        apartmentsDataBase();
        ordersDataBase();
    }
}
