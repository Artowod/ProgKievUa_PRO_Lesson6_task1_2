import java.sql.*;
import java.util.Random;

//3. Создать проект «База данных заказов». Создать
//        таблицы «Товары» , «Клиенты» и «Заказы».
//        Написать код для добавления новых клиентов,
//        товаров и оформления заказов.


public class OrdersDBManager {
    private final String GOODSTABLE = "Goods";
    private final String CLIENTSTABLE = "Clients";
    private final String ORDERSTABLE = "Orders";
    private Connection connection;

    public OrdersDBManager(Connection connection) {
        this.connection = connection;
    }

    public OrdersDBManager() {
    }

    public void createTablesInDB() {
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS=0");
                statement.execute("DROP TABLE IF EXISTS " + GOODSTABLE);
                statement.execute("SET FOREIGN_KEY_CHECKS=1");
                statement.execute("CREATE TABLE " + GOODSTABLE + " (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(30) NOT NULL, " +
                        "stock INT NOT NULL, " +
                        "prise INT NOT NULL)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS=0");
                statement.execute("DROP TABLE IF EXISTS " + CLIENTSTABLE);
                statement.execute("SET FOREIGN_KEY_CHECKS=1");
                statement.execute("CREATE TABLE " + CLIENTSTABLE + " (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(30) NOT NULL," +
                        "address VARCHAR(30))");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS=0");
                statement.execute("DROP TABLE IF EXISTS " + ORDERSTABLE);
                statement.execute("SET FOREIGN_KEY_CHECKS=1");
                statement.execute("CREATE TABLE " + ORDERSTABLE + " (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "good_id INT NOT NULL, " +
                        "client_id INT NOT NULL, " +
                        "CONSTRAINT good_id FOREIGN KEY (good_id) REFERENCES " + GOODSTABLE + " (id), " +
                        "CONSTRAINT client_id FOREIGN KEY (client_id) REFERENCES " + CLIENTSTABLE + " (id)) ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putInitialDataToTables() {
        String goods = "Hammer,Nails,Car,Flat,Building,StarShip";
        String clients = "FatBoy,TinyLady,BeautyMadam,Boy,Girl,Boss";
        String address = "Kiev,Lviv,Zhytomir,Kharkiv,Dnepr,Lutsk";

        try {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + GOODSTABLE + " (name,stock,prise) VALUES (?,?,?)")) {
                for (int i = 0; i < goods.split(",").length; i++) {
                    statement.setString(1, goods.split(",")[i]);
                    statement.setInt(2, new Random().nextInt(300));
                    statement.setInt(3, 1000 + new Random().nextInt(10000));
                    statement.executeUpdate();
                }
            }
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + CLIENTSTABLE + " (name,address) VALUES (?,?)")) {
                for (int i = 0; i < clients.split(",").length; i++) {
                    statement.setString(1, clients.split(",")[i]);
                    statement.setString(2, address.split(",")[new Random().nextInt(6)]);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addNewOrderByID(int good, int client) {
        System.out.print("Add new Order (good = " + good + ", client = " + client + "): ");
        String queryOne = "INSERT INTO " + ORDERSTABLE + " (good_id, client_id) VALUES(?,?)";
        try {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(queryOne)) {
                    statement.setInt(1, good);
                    statement.setInt(2, client);
                    statement.executeUpdate();
                    connection.commit();
                    System.out.println("Ok");
                } catch (SQLException e) {
                    System.out.println("Transaction <AddNewOrder> failed. RolledBack.");
                    connection.rollback();
                }
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIDByName(String targetTable, String recordName) {
        String getTableQuery = "SELECT * FROM " + targetTable + " WHERE name='" + recordName + "'";
        try (PreparedStatement statement = connection.prepareStatement(getTableQuery)) {
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                return -1;
            } else {
                return Integer.valueOf(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addNewOrderByName(String good, String client) {
        System.out.print("Add new Order by Names (good = " + good + ", client = " + client + "): ");
        int goodID = getIDByName(GOODSTABLE, good);
        int clientID = getIDByName(CLIENTSTABLE, client);

        if (goodID == -1 || clientID == -1) {
            System.out.println("Transaction <AddNewOrder> can`t be executed - wrong foreign key.");
        } else {
            System.out.print(" -> ");
            addNewOrderByID(goodID, clientID);
        }
    }

    public void addNewClient(String newClientName, String address) {
        System.out.print("Add new Client (client = " + newClientName + ", address = " + address + "): ");
        String queryOne = "INSERT INTO " + CLIENTSTABLE + " (name, address) VALUES(?,?)";
        try {
            connection.setAutoCommit(false); // enable transactions
            try {
                try (PreparedStatement statement = connection.prepareStatement(queryOne)) {
                    statement.setString(1, newClientName);
                    statement.setString(2, address);
                    statement.executeUpdate();
                    connection.commit();
                    System.out.println("Ok");
                } catch (SQLException e) {
                    // e.printStackTrace();
                    System.out.println("Transaction <AddNewClient> failed. RolledBack.");
                    connection.rollback();
                }
            } finally {
                connection.setAutoCommit(true); // return to default mode
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addNewGood(String newGoodName, int stockQuantity, int prise) {
        System.out.print("Add new Good (good = " + newGoodName + ", stock = " + stockQuantity + "): ");
        String queryOne = "INSERT INTO " + GOODSTABLE + " (name, stock, prise) VALUES(?,?,?)";
        try {
            connection.setAutoCommit(false); // enable transactions
            try {
                try (PreparedStatement statement = connection.prepareStatement(queryOne)) {
                    statement.setString(1, newGoodName);
                    statement.setInt(2, stockQuantity);
                    statement.setInt(3, prise);
                    statement.executeUpdate();
                    connection.commit();
                    System.out.println("Ok");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    System.out.println("Transaction <AddNewGood> failed. RolledBack.");
                    connection.rollback();
                }
            } finally {
                connection.setAutoCommit(true); // return to default mode
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

    public boolean showTable(String tableName) {
        if (!tableName.equals(CLIENTSTABLE) && !tableName.equals(GOODSTABLE) && !tableName.equals(ORDERSTABLE)) {
            System.out.println("You are requesting missing Table. Please check Table name");
            return false;
        } else {
            System.out.println("\nShow table (" + tableName + "):");
            String query = "SELECT * FROM " + tableName;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (!resultSet.next()) {
                    System.out.println("ResultSet is Empty !");
                } else {
                    showResultSet(resultSet);

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("Something wrong with showTable method");
            }
        }
        return true;
    }

//    CREATE TABLE `mysqlhometask2`.`new_table` (
//      `idnew_table` INT NOT NULL AUTO_INCREMENT,
//      `goods_id` INT NOT NULL,
//      `clients_id` INT NOT NULL,
//    PRIMARY KEY (`idnew_table`),
//    INDEX `goods_id_idx` (`goods_id` ASC),
//    INDEX `client_id_idx` (`clients_id` ASC),
//    CONSTRAINT `goods_id`
//    FOREIGN KEY (`goods_id`)
//    REFERENCES `mysqlhometask2`.`goods` (`id`)
//    ON DELETE NO ACTION
//    ON UPDATE NO ACTION,
//    CONSTRAINT `client_id`
//    FOREIGN KEY (`clients_id`)
//    REFERENCES `mysqlhometask2`.`clients` (`id`)
//    ON DELETE NO ACTION
//    ON UPDATE NO ACTION);

}
