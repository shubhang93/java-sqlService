import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

/*
 * SqlService, 2018-06-17
 */


public class SqlService {
    private HashMap<String, String> queryMap;
    private SqlExecutorService sqlExecutorService;

    SqlService(String sqlFilesPath, DataSource ds) {
        System.out.println("Reading queries from --> " + sqlFilesPath);
        this.queryMap = new QueryMapBuilder(sqlFilesPath).getQueryMap();
        this.sqlExecutorService = new SqlExecutorService(ds);
    }

    List<HashMap<String, Object>> executeSql(String queryName, HashMap<String, Object> params) {
        String queryStatement = queryMap.get(queryName);
        if (queryStatement == null) {
            try {
                throw new Exception("Query name " + queryName + " not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Executing --> " + queryStatement + " with params " + params);
        return sqlExecutorService.executeSql(queryStatement, params);

    }

}

