package pos;
import java.sql.*;

import javax.swing.JOptionPane;

public class ConnectDB {
	// Oracle user ID
	private String id;
	// Password
	private String pwd; 
	// Connection
	private static Connection db;
	
	// 생성자
	public ConnectDB(String id, String pwd) {
		this.id = id;
		this.pwd = pwd;
		connect();
	}
	
	// Connect
	private void connect() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			db = DriverManager.getConnection("jdbc:oracle:thin:"
					+ "@localhost:1521:XE", id, pwd);
			System.out.println("데이터베이스에 연결하였습니다.");
			new Viewer(db);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "데이터베이스 연결에 실패하였습니다.");
			System.out.println("SQLException :" + e);
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			JOptionPane.showMessageDialog(null, "데이터베이스 연결에 실패하였습니다.");
		}
	}
	
	public Connection getConnect() {
		return db;
	}
}
