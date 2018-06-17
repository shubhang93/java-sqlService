import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SqlExecutorService {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    SqlExecutorService(DataSource ds) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
    }

    private List<HashMap<String, Object>> resultSetTransformer(ResultSet rs, ResultSetMetaData rsMetaData) {
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        try {
            int columnCount = rsMetaData.getColumnCount();
            while (rs.next()) {
                HashMap<String, Object> rowMap = new HashMap<>();
                for (int i = 1; i <= columnCount; ++i) {
                    rowMap.put(rsMetaData.getColumnName(i), rs.getObject(i));
                }
                result.add(rowMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    List<HashMap<String, Object>> executeSql(String queryStmt, HashMap<String, Object> params) {
        return namedParameterJdbcTemplate.execute(queryStmt, params, ps -> {
            ResultSet rs = null;
            ResultSetMetaData resultSetMetaData = null;
            try {
                rs = ps.executeQuery();
                resultSetMetaData = rs.getMetaData();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            assert resultSetMetaData != null;
            return resultSetTransformer(rs, resultSetMetaData);
        });

    }


}
