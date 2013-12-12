package newbieilmu.rawatinap;

import javax.swing.*;
import java.sql.*;

public class Koneksi
{

    public Koneksi()
    {
    }

	public Connection open_a_Connection() throws SQLException
	{
		Connection connect= null;
		try
		{
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/db_rawatinap","root","root");
		return connect;
		}
		catch(SQLException ex)
		{
		JOptionPane.showMessageDialog(null,"Tidak ada koneksi yang terbuka"+ ex,"informasi",JOptionPane.INFORMATION_MESSAGE);
		return null;
		}

		catch(Exception e)
		{
		JOptionPane.showMessageDialog(null,"Tidak dapat membuka koneksi yang terbuka","informasi",JOptionPane.INFORMATION_MESSAGE);
		return null;
		}
	}


}

