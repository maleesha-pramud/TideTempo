import com.wigerlabs.tidetempo.connection.MySQL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DescTimeLog {
    public static void main(String[] args) throws Exception {
        ResultSet rs = MySQL.execute("SELECT * FROM time_log LIMIT 1");
        ResultSetMetaData md = rs.getMetaData();
        for (int i=1; i<=md.getColumnCount(); i++) {
            System.out.println(md.getColumnName(i) + " : " + md.getColumnTypeName(i));
        }
    }
}
