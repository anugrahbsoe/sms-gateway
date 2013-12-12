package newbieilmu.rawatinap;

import javax.swing.*;
import javax.comm.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;



import com.jtattoo.plaf.mcwin.McWinLookAndFeel; 

public class Broadcast extends JFrame implements ActionListener{
	
	Koneksi objKoneksi = new Koneksi();
	Connection con ;
	Statement state;
	ResultSet rs;
	String sql;
	
	private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
	//private JLabel lblPasien = new JLabel("Nama");
	//private JTextField txtPasien = new JTextField("");
	private JLabel lblNoTelp = new JLabel("To");
	private JTextField txtNoTelp = new JTextField("");
	private JLabel lbltxAPesan = new JLabel("Message text");
	private JTextArea txAPesan = new JTextArea("");
	private JButton btnKirim = new JButton("Kirim",new ImageIcon("src/newbieilmu/rawatinap/image/kirim.jpeg"));
	private JButton btnKeluar = new JButton("Keluar",new ImageIcon("src/newbieilmu/rawatinap/image/close.jpeg"));
	private JButton btnBersih = new JButton("Bersih",new ImageIcon("src/newbieilmu/rawatinap/image/clear.jpg"));
	
	public Broadcast(){
	super("Broadcast");
	setLocation(d.height/2-getHeight()/2,d.width/2-getWidth()/2);
	setIconImage(getToolkit().getImage("src/newbieilmu/rawatinap/image/kurir.jpeg"));
	setSize(500,300);
	setVisible(true);
	getContentPane().setLayout(null);
	
	//getContentPane().add(lblPasien);
	//getContentPane().add(txtPasien);
	getContentPane().add(lblNoTelp);
	getContentPane().add(txtNoTelp);
	getContentPane().add(lbltxAPesan);
	getContentPane().add(txAPesan);
	getContentPane().add(btnKirim);
	getContentPane().add(btnKeluar);
	getContentPane().add(btnBersih);
	
	//lblPasien.setBounds(10,10, 120,20);
	//txtPasien.setBounds(120, 10, 200,20);
	lblNoTelp.setBounds(10,40, 120,20);
	txtNoTelp.setBounds(120, 40, 200,20);
	lbltxAPesan.setBounds(10,80, 120,20);
	txAPesan.setBounds(120, 80, 300, 100);
	btnKirim.setBounds(120, 200, 120,25);	
	btnKeluar.setBounds(260, 200, 120,25);
	btnBersih.setBounds(120, 230, 120,25);
	
	//txtPasien.addActionListener(this);
	txtNoTelp.addActionListener(this);
	btnKirim.addActionListener(this);
	btnKeluar.addActionListener(this);
	btnBersih.addActionListener(this);
	
	}
	
	InputStream        input;
	OutputStream       output;
	private void kirimAT(String atCmd, int delay) {
  	Boolean tungguDelay = new Boolean(true);
  	boolean getDelay = false;
  	// Membuat antrian proses
  	synchronized (tungguDelay) {
    		try
    		{
      		// Menulis AT Commmand
      		output.write( (atCmd).getBytes());
      		output.flush();// Hapus OutputStream
    		} //Akhir Try
   		catch (IOException e) {
   			System.out.println (e.getMessage());
   		}
    		try
    		{
      		tungguDelay.wait(delay);
    		} // Akhir try
    		catch (InterruptedException ie)
    		{
    			System.out.println (ie.getMessage());
      		getDelay = true;
    		} // Akhir catch
  	} // Akhir syncronized
	}
	
	
	
	private static StringBuffer pesanPDUKirim     = null;
	private static int          panjangNotlpTujuan= 0;
	private static int          panjangPesanKirim = 0;
	private static String       PduPesan          = null;

	private static int          panjangKarakter= 0;
	private static StringBuffer stringBuffer   = null;

	
	private static String balikKarakter(String karakter) {
  	panjangKarakter = karakter.length();
  	stringBuffer = new StringBuffer(panjangKarakter);
  	for (int i = 0; (i + 1) < panjangKarakter; i = i + 2) {
    		stringBuffer.append(karakter.charAt(i + 1));
    		stringBuffer.append(karakter.charAt(i));
  	}
  	return new String(stringBuffer);
	}
	
	private static char[] hexa;
	private static char[] karakter;
	
	private static String PduKirimSms(String notlp, String pesan) {
  	pesanPDUKirim = new StringBuffer(320); // 320 = 160 * 2 (panjang max)
  	// Tambahkan nilai PDU Type ---> Default = 11
  	pesanPDUKirim.append("11");
  	// Tambahkan nilai MR ---> Default = 00
  	pesanPDUKirim.append("00");
  	// Tambahkan nilai panjang nomor pengirim
  	panjangNotlpTujuan = notlp.length();
  	pesanPDUKirim.append(rubahKeHexa(panjangNotlpTujuan));
  	// Tambah nilai format no. telepon --> format internasional = 91
  	pesanPDUKirim.append("91");
  	// Tambahkan nilai nomor telepon pengirim
  	// Jika panjang notlp adalah ganjil
  	if ( (notlp.length() % 2) == 1) {
    		notlp = balikKarakter(notlp + "F");
  	}
  	// Jika panjang notlp adalah genap
  	else {
    		notlp = balikKarakter(notlp);
  	}
  	pesanPDUKirim.append(notlp);
  	// tambahkan nilai PID ---> Default = 00
  	pesanPDUKirim.append("00");
  	// tambahkan nilai DCS ---> Default = 00
  	pesanPDUKirim.append("00");
  	// tambahkan nilai VP = 4 hari ---> AA h
  	pesanPDUKirim.append("AA");
  	panjangPesanKirim = pesan.length();
  	PduPesan = tujuhKeDelapanBit(pesan);
  	System.out.println ("pesan "+pesan);
  	System.out.println ("PDU Pesan "+PduPesan);
  	pesanPDUKirim.append(rubahKeHexa(panjangPesanKirim));
  	pesanPDUKirim.append(PduPesan);

  	return new String(pesanPDUKirim);

	}
	
	private static String rubahKeHexa(int a) {
  	char[] hexa = {
      	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
      	'E', 'F'};

  	karakter = new char[2];
  	// Mengambil hanya 8 bit 255d = 11111111 b
  	a = a & 255;
  	// hasil pembagian dengan 16
  	karakter[0] = hexa[a / 16];
  	System.out.println ("karakter "+karakter[0]);
  	// sisa hasil pembagian dengan 16
  	karakter[1] = hexa[a % 16];
  	System.out.println ("karakter "+karakter[1]);

  	return new String(karakter);
	}
	
	private static char[] asciiToGsmMap;
	private static String tujuhKeDelapanBit(String pesan) {

  	StringBuffer msg = new StringBuffer(pesan);

  	StringBuffer encmsg = new StringBuffer(2 * 160);
  	int bb = 0, bblen = 0, i;
  	char o = 0, c = 0, tc;

  	for (i = 0; i < msg.length() || bblen >= 8; i++) {
    		if (i < msg.length()) {
      		c = msg.charAt(i);
      		System.out.println ("asciiToGsmMap "+asciiToGsmMap[c]);
      		tc = asciiToGsmMap[c];

      		c = tc;
				System.out.println ("c= ");
      		c &= ~ (1 << 7);
//      		System.out.println ("1<<7 "+1<<7);
      		System.out.println ("c= "+c);
      		bb |= (c << bblen);
      		bblen += 7;
    		}

    		while (bblen >= 8) {
      		o = (char) (bb & 255);
      		encmsg.append(rubahKeHexa(o));
      		bb >>>= 8;
      		bblen -= 8;
    		}
  	}
  	if ( (bblen > 0)) {
    		encmsg.append(rubahKeHexa(bb));
  	}
  	return encmsg.toString();
	}
	
	private static char[] gsmToAsciiMap; // GSM ==> ASCII
	
	static {
  	final int lastindex = 255;
  	gsmToAsciiMap = new char[lastindex + 1];
  	asciiToGsmMap = new char[lastindex + 1];
  	int i;

  	for (i = 0; i <= lastindex; i++) {
    		gsmToAsciiMap[i] = asciiToGsmMap[i] = (char) i;
  	}
	}

	
	private void prosesKirimSms(String notlp, String pesan) {
  	//if(cekNomor()) {
	    	try {
	      		// Merubah pesan menjadi Format PDU (Protocol Data Unit)
	      		String pesanPDUKirim = PduKirimSms(notlp.trim(), pesan.trim());
	      		System.out.println ("Pesan dikirim : "+pesanPDUKirim);

	      		// Proses Mengirim Pesan
	      		kirimAT("AT+CMGS=" + (pesanPDUKirim.length() / 2) + "\15", 500);
	      		kirimAT("00" + pesanPDUKirim, 2500); // Kirim Pesan Format PDU
	      		kirimAT("\032", 100); // Ctrl + Z

	      		JOptionPane.showMessageDialog(Broadcast.this,
	      									"Pesan Dikirim ke : "+notlp+
	      									"\nIsi pesan : "+pesan,
	      									"Pesan dikirim",
	      									JOptionPane.INFORMATION_MESSAGE);

	      		// Berikan waktu sleep agar terminal siap kembali untuk mengirim pesan
	      		Thread.currentThread().sleep(10000);

	    	} // Akhir try
	    	catch (Exception e) {
	    		System.out.println (e.getMessage());
	    	}
  //	}
  //	else {}
	}
	
	private String infoSmsc          = null;
  	private int    nilaiSmsc         = 0;
  	private int    nomorSmsc         = 0;
  	private String panjangNotlp      = null;
  	private int    nilaiPanjangNotlp = 0;
  	private int    nilaiNotlp        = 0;
  	private String Notlp             = null;
  	private String dapatNotlp        = null;
  	private String panjangPesan      = null;
  	private int    nilaiPanjangPesan = 0;
  	private String pesanPDU          = null;
  	private String pesan             = null;
	
	public boolean cekNomor() {
		if(txtNoTelp.getText().trim().equals("") || txAPesan.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(Broadcast.this,
									"Isi pesan atau nomor hp tujuan terlebih dahulu",
									"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else if(!txtNoTelp.getText().matches("[0-9]*")) {
			JOptionPane.showMessageDialog(Broadcast.this,
									"nomor hp tujuan belom bener tuch!",
									"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else
		{
			return true;
		}
	}
	
	void bersih(){
		txtNoTelp.setText("");
		//txtPasien.setText("");
		txAPesan.setText("");
	}
	
	void broadcast(){
		try
		{
		Connection Koneksi = objKoneksi.open_a_Connection();
		state = Koneksi.createStatement();
		sql ="select NoTelp from anggota where Status = 1";
		rs = state.executeQuery(sql);
		System.out.println(sql);
			while(rs.next())
			{
				//txtNoTelp.setText(rs.getString(1));
				//txtPasien.setText(rs.getString(2));
				//txAPesan.setText(rs.getString(3));
			prosesKirimSms(rs.getString(1),txAPesan.getText());
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik");// + pesan);
			}
		state.close();
		Koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==txtNoTelp){
			broadcast();
		}
		if(arg0.getSource()==btnKeluar){
			JOptionPane.showMessageDialog(null, "Yakin ingin Keluar?");
			System.exit(0);
		}
		if(arg0.getSource()==btnKirim){
			//prosesKirimSms(txtNoTelp.getText().trim(), txAPesan.getText().trim().toLowerCase());
			broadcast();
		}
		if(arg0.getSource()==btnBersih){
			bersih();
		}
	}

public static void main(String[] args) throws Exception, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		
		UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
		new Broadcast();
	}
	
}
	
	
	