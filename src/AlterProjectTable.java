import com.wigerlabs.tidetempo.connection.MySQL;

public class AlterProjectTable {
    public static void main(String[] args) throws Exception {
        MySQL.execute("ALTER TABLE project ADD COLUMN estimated_hours DOUBLE DEFAULT 0");
        System.out.println("Column added successfully.");
    }
}
