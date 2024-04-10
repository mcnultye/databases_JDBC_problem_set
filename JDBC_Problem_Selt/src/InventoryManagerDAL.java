import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryManagerDAL {
    private Connection connection;

    private void InitializeConnection(String databaseName, String user, String password)
    {
        try
        {
            if(connection == null)
            {
               connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, user, password);
            }
        } 
        catch (SQLException exception)
        {
            System.out.println("Failed to connect to the database" + exception.getMessage());
        }
    }

    public InventoryManagerDAL(String databaseName, String username, String password) {
        InitializeConnection(databaseName, username, password);
    }


     // Method to search inventory based on filters
    public ResultSet searchInventory(int dangerLevelFilter, String magicTypeFilter, BigDecimal maxPriceFilter) throws SQLException {
        String sql = "SELECT itemName, quantity, itemDescription, associatedMagic, dangerLevel, pricePaid, priceSelling " +
                     "FROM Item " +
                     "WHERE (? = 0 OR dangerLevel = ?) " +
                     "AND (? IS NULL OR associatedMagic = ?) " +
                     "AND (? = 0 OR priceSelling <= ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, dangerLevelFilter);
        statement.setInt(2, dangerLevelFilter);
        statement.setString(3, magicTypeFilter);
        statement.setString(4, magicTypeFilter);
        statement.setBigDecimal(5, maxPriceFilter);
        statement.setBigDecimal(6, maxPriceFilter);
        return statement.executeQuery();
    }

    // Method to insert a new inventory item
    public boolean addNewItem(String itemName, int itemQuantity, String itemDescription, String itemMagic, int itemDangerLevel, BigDecimal itemPricePaid, BigDecimal itemPriceSelling) {
        try {
            CallableStatement addNewItemProcedure = connection.prepareCall("{CALL addNewItem(?, ?, ?, ?, ?, ?, ?)}");
            addNewItemProcedure.setString(1, itemName);
            addNewItemProcedure.setInt(2, itemQuantity);
            addNewItemProcedure.setString(3, itemDescription);
            addNewItemProcedure.setString(4, itemMagic);
            addNewItemProcedure.setInt(5, itemDangerLevel);
            addNewItemProcedure.setBigDecimal(6, itemPricePaid);
            addNewItemProcedure.setBigDecimal(7, itemPriceSelling);
            addNewItemProcedure.execute();
            return true;
        } catch (SQLException ex) {
            System.out.println("Failed to add new item: " + ex.getMessage());
            return false;
        }
    }

      // Method to search and display inventory based on user filters
      public void viewInventory(int dangerLevelFilter, String magicTypeFilter, double maxPriceFilter) {
        try {
            // Prepare SQL query based on user filters
            String sql = "SELECT itemName, quantity, itemDescription, associatedMagic, dangerLevel, priceSelling " +
                         "FROM Item " +
                         "WHERE (? = 0 OR dangerLevel = ?) " +
                         "AND (? IS NULL OR associatedMagic = ?) " +
                         "AND (? = 0 OR priceSelling <= ?)";
            
            // Create prepared statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, dangerLevelFilter);
            statement.setInt(2, dangerLevelFilter);
            statement.setString(3, magicTypeFilter);
            statement.setString(4, magicTypeFilter);
            statement.setDouble(5, maxPriceFilter);
            statement.setDouble(6, maxPriceFilter);
            
            // Execute query
            ResultSet resultSet = statement.executeQuery();
            
            // Display inventory items
            System.out.println("Inventory Items:");
            while (resultSet.next()) {
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Quantity: " + resultSet.getInt("quantity"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Magic: " + resultSet.getString("magic"));
                System.out.println("Danger Level: " + resultSet.getInt("dangerLevel"));
                System.out.println("Price Sell: " + resultSet.getDouble("priceSelling"));
                System.out.println();
            }
            
            // Close resources
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getSalesReport() throws SQLException {
        ResultSet resultSet = null;
        try {
            // Prepare SQL query to retrieve sales data
            String query = "SELECT itemName, customerName, saleDate, price, magicType, dangerLevel FROM sale";
            PreparedStatement statement = connection.prepareStatement(query);

            // Execute query and retrieve results
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}