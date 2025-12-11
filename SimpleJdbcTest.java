import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SimpleJdbcTest {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/parking_lot";
        String username = "root";
        String password = "Nabhyadav@682";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(url, username, password);
            System.out.println("✅ MySQL Connected!");

            String sql = "SELECT * FROM slots";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getInt("slot_number") + " | " +
                    rs.getString("vehicle_type") + " | " +
                    rs.getBoolean("occupied")
                );
            }

            con.close();
            System.out.println("✅ Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
