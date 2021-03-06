package model;

import exeption.InvalidException;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class DBtest {
    public static final String DB_NAME = "test";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "pass";
    private Connection connectToDB;
     private Statement st;

    @BeforeClass
    public static void setupDataBase() throws SQLException, InvalidException {
        new DataBaseResources().before();
    }

    @AfterClass
    public static void dropDataBase() throws SQLException, InvalidException {
        new DataBaseResources().after();
    }

    @Before
    public void testGetAllTables() throws SQLException, InvalidException, ClassNotFoundException {
        connectToDB = new DbConnection().connection(DB_NAME, USER_NAME, PASSWORD);
        st = connectToDB.createStatement();
    }

    @After
    public void closeConnect() throws SQLException {
        connectToDB.close();
    }

    @Test
    public void testSelect() throws SQLException {

        List<String> select = new Select().select("users", st);
        assertEquals(5, select.size());
        st.close();
    }

    @Test
    public void testGetColumns() throws SQLException {

        String[] columns = new Select().getTableColumns("users", st);
        assertEquals("[id, name, password]", Arrays.toString(columns));
        st.close();
    }

    @Test
    public void testSelectAllTables() throws SQLException {
        String[] selectAll = new SelectTablesList().selectAllTable(st);
        assertEquals("[users, assets]", Arrays.toString(selectAll));
        st.close();
    }

    @Test
    public void testCRUD() throws SQLException {
        //delete
        st = connectToDB.createStatement();
        new InsertUpdateDeleteCreate().run(st, "delete from users where id = 5");
        st.close();

        //insert
        st = connectToDB.createStatement();
        new InsertUpdateDeleteCreate().run(st, "insert into users values(5, 'User5', 5555)");
        st.close();

        //update
        st = connectToDB.createStatement();
        new InsertUpdateDeleteCreate().run(st, "update users set password = '9999' where id = 5");
        st.close();

        //select
        st = connectToDB.createStatement();
        List<String> select = new Select().select("users where id = 5", st);
        assertEquals("[5, User5, 9999]", Arrays.toString(select.toArray()));
        st.close();
    }
}
