package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Conexion {

	private Connection con;
	
	public Conexion(){
		
		try {
			Class.forName("org.neo4j.jdbc.Driver");
			
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost:7474");
			
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
				
	}
	
	public Connection getConnection(){
		return con;
	}
}
