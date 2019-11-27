package cn.edu.sdjzu.xg.bysj.dao;
import cn.edu.sdjzu.xg.bysj.domain.*;
import cn.edu.sdjzu.xg.bysj.service.*;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;
public final class TeacherDao {
	private static TeacherDao teacherDao=new TeacherDao();
	private TeacherDao(){}
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	private static Collection<Teacher> teachers;

	public Collection<Teacher> findAll()throws SQLException{
    teachers = new TreeSet<Teacher>();
	  Connection connection= JdbcHelper.getConn();
    //执行sql查询语句，并获得结果集
    Statement stmt=connection.createStatement();
    ResultSet resultSet=stmt.executeQuery("select * from teacher");

    while(resultSet.next()) {
      Teacher teacher = new Teacher(resultSet.getInt("id"),
        resultSet.getString("name"),
        ProfTitleService.getInstance().find(resultSet.getInt("profTitle_id")),
        DegreeService.getInstance().find(resultSet.getInt("degree_id")),
        DepartmentService.getInstance().find(resultSet.getInt("department_id"))
      );
      teachers.add(teacher);
    }
    JdbcHelper.close(stmt,connection);
    return TeacherDao.teachers;

	}

	public Teacher find(Integer id)throws SQLException{
    TeacherDao.getInstance().findAll();
		Teacher desiredTeacher = null;
		for (Teacher teacher : teachers) {
			if(id.equals(teacher.getId())){
				desiredTeacher =  teacher;
				break;
			}
		}
		return desiredTeacher;
	}

	public boolean update(Teacher teacher)throws SQLException{
    Connection connection=JdbcHelper.getConn();
    String updateTeacher_sql="UPDATE teacher SET name=?,profTitle_id=?,degree_id=?,department_id=? where id=?";
    PreparedStatement pstmt=connection.prepareStatement(updateTeacher_sql);
    pstmt.setString(1,teacher.getName());
    pstmt.setInt(2,teacher.getTitle().getId());
    pstmt.setInt(3,teacher.getDegree().getId());
    pstmt.setInt(4,teacher.getDepartment().getId());
    pstmt.setInt(5,teacher.getId());
    int affectedRowNum=pstmt.executeUpdate();
    System.out.println("修改了"+ affectedRowNum + "条记录");
    JdbcHelper.close(pstmt,connection);
    return affectedRowNum>0;
	}

	public boolean add(Teacher teacher)throws SQLException{
    Connection connection=JdbcHelper.getConn();
    String addTeacher_sql="INSERT INTO teacher(name,profTitle_id,degree_id,department_id) VALUES(?,?,?,?)";
    //在该连接上创建预编译语句对象
    PreparedStatement pstmt=connection.prepareStatement(addTeacher_sql);
    pstmt.setString(1,teacher.getName());
    pstmt.setInt(2,teacher.getTitle().getId());
    pstmt.setInt(3,teacher.getDegree().getId());
    pstmt.setInt(4,teacher.getDepartment().getId());
    //执行预编译对象的executeUpdate方法，获取添加的记录行数
    int affectedRowNum=pstmt.executeUpdate();
    System.out.println("添加了"+ affectedRowNum + "条记录");
    //关闭pstmt对象
    //关闭connection对象
    JdbcHelper.close(pstmt,connection);
    return affectedRowNum>0;
	}

	public boolean delete(Integer id)throws SQLException{
		Teacher teacher = this.find(id);
		return this.delete(teacher);
	}

	public boolean delete(Teacher teacher)throws SQLException{
    Connection connection=JdbcHelper.getConn();
    String deleteTeacher_sql="DELETE FROM teacher WHERE ID=?";
    PreparedStatement pstmt=connection.prepareStatement(deleteTeacher_sql);
    pstmt.setInt(1,teacher.getId());
    pstmt.executeUpdate();
    //执行预编译对象的executeUpdate方法，获取删除的记录行数
    int affectedRowNum=pstmt.executeUpdate();
    System.out.println("删除了" + affectedRowNum + "条记录");
    JdbcHelper.close(pstmt,connection);
    return teachers.remove(teacher);
	}

  public static void main(String[] args) throws SQLException{
    //TeacherDao.getInstance().findAll();
    /*ProfTitle profTitle=ProfTitleService.getInstance().find(1);
    Department department=DepartmentService.getInstance().find(2);
    Degree degree=DegreeService.getInstance().find(15);
    Teacher teacher1=new Teacher("苏童",profTitle,degree,department);
    TeacherDao.getInstance().add(teacher1);*/
    Teacher teacher2=TeacherDao.getInstance().find(2);
    System.out.println(teacher2);
    teacher2.setName("王明");
    System.out.println(teacher2);
    TeacherDao.getInstance().update(teacher2);
    System.out.println(teacher2);
  }

}
