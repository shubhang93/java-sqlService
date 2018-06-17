import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.sql.DataSource;
import java.lang.reflect.Method;
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



    private List<HashMap<String, Object>> fetchResults(String queryName, HashMap<String, Object> params) {
        System.out.println(queryMap);
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

    List<HashMap<String, Object>> executeSql(String queryName, HashMap<String, Object> params) {
        return this.fetchResults(queryName, params);

    }


    List<HashMap<String, Object>> executeSql(String queryName) {
        return this.fetchResults(queryName, null);
    }

}

