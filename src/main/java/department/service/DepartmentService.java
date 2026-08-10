package department.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import department.dao.DepartmentDao;
import department.model.Department;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class DepartmentService {
    private DepartmentDao departmentDao = new DepartmentDao();

    public List<Department> getDepartmentList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return departmentDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDepartment(String deptName) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            departmentDao.insert(conn, deptName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeDepartment(int deptId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            departmentDao.delete(conn, deptId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAllDepartments() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            departmentDao.deleteAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}