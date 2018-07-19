import java.sql.*;
import java.util.Random;

public class ApartmentsDBManager {
    private final String TABLENAME = "Apartments";
    private Connection connection;

    public ApartmentsDBManager(Connection connection) {
        this.connection = connection;
    }

    public void createTableInDB() {
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE IF EXISTS " + TABLENAME);
                statement.execute("CREATE TABLE " + TABLENAME + " (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "district VARCHAR(30) NOT NULL, " +
                        "street VARCHAR(30) NOT NULL, " +
                        "squaremeters INT NOT NULL, " +
                        "roomsnumber SMALLINT NOT NULL, " +
                        "prise INT NOT NULL)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putInitialDataToTable() {
        String districts = "Dneprovskyi,Darnitskyi,Podol,Sviatosh,Teremki,Troeshina";
        String streets = "Boychenka,Miloslavska,Malishko,Shalett,Khreshatik,Akhmatovoj";

        try {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Apartments (district,street,squaremeters,roomsnumber,prise) VALUES (?,?,?,?,?)")) {
                for (int i = 0; i < 10; i++) {
                    statement.setString(1, districts.split(",")[new Random().nextInt(6)]);
                    statement.setString(2, streets.split(",")[new Random().nextInt(6)]);
                    statement.setInt(3, 20 + new Random().nextInt(300));
                    statement.setInt(4, 1 + new Random().nextInt(5));
                    statement.setInt(5, 10000 + new Random().nextInt(1000000));
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.print(metaData.getColumnName(i) + "\t\t");
            }
            System.out.println();
            do {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.print(resultSet.getString(i) + "\t\t");
                }
                System.out.println();
            } while (resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getDataFromDBByParameters(String[] incomingRequests) {
//        String str = "SELECT * FROM Apartments WHERE district='Podol'";
        String queryOne = "SELECT * FROM Apartments WHERE ";
        if (incomingRequests.length == 1) {
            queryOne = queryOne + incomingRequests[0];
        } else {
            for (String str : incomingRequests) {
                queryOne = queryOne + str + " AND ";
            }
            queryOne = queryOne.substring(0,queryOne.length()-5);
        }
        System.out.println(" > " + queryOne);
        try (PreparedStatement statement = connection.prepareStatement(queryOne)) {

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                System.out.println("ResultSet is Empty !");
            } else {
                showResultSet(rs);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}
