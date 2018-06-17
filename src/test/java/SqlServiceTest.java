/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class SqlServiceTest {

    private DataSource ds;
    private SqlService sqlService;

    @Before
    public void setup() {
        ds = new JdbcDataSource();
        ((JdbcDataSource) ds).setURL("jdbc:h2:./src/test/resource/db/test");
        ((JdbcDataSource) ds).setUser("sa");
        ((JdbcDataSource) ds).setPassword("sa");
        initializeSqlService();
        setupTables();
        setupData();
    }

    private void initializeSqlService() {
        sqlService = new SqlService("/home/shubhang/opensource/sql-service/src/test/resource", ds);
    }


    private void setupTables() {
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("drop table product");
            stmt.execute("create table product (id int primary key , code varchar(20),name varchar(20))");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupData() {
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("insert into product values(1,'p234','soap')");
            stmt.execute("insert into product values (2,'p456','soup')");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void shouldReturnAList() {
        List<HashMap<String, Object>> res = sqlService.executeSql("product");
        System.out.println(res);
        assertTrue("No records", !res.isEmpty());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void shouldThrowWhenNoQueryParamsArePassed() {
        List<HashMap<String, Object>> res = sqlService.executeSql("productWithParams");
    }

    @Test
    public void shouldReturnOneProductWhereCodeIs456() {
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("code", "p456");
        row.put("name", "soup");
        HashMap<String, Object> params = new HashMap<>();
        params.put("code", "p456");
        List<HashMap<String, Object>> res = sqlService.executeSql("productWithParams", params);
        HashMap<String, Object> resRow = res.get(0);
        assert (row.equals(resRow));
    }
}
