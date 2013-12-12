package newbieilmu.rawatinap;

import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import javax.swing.*;
import javax.comm.SerialPortEvent.*;
import javax.comm.*;
import javax.swing.border.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.TooManyListenersException;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.sql.*;

import com.jtattoo.plaf.mcwin.McWinLookAndFeel;//setting look and feel


public class rawatInap extends JFrame implements ActionListener,SerialPortEventListener
{
    Koneksi objKoneksi = new Koneksi();
	Connection koneksi ;
	Statement state;
	ResultSet rs;
	String sql;
    Container konten = getContentPane();

	private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    //private JLabel lblJudul = new JLabel("Rawat Inap",SwingConstants.CENTER);
    private JButton btnClear = new JButton("Bersihkan",new ImageIcon("src/newbieilmu/rawatinap/image/clear.jpg"));
    private JButton btnTestConf = new JButton ("Test Konfigurasi",new ImageIcon("src/newbieilmu/rawatinap/image/Test.jpeg"));
    private JButton btnConf = new JButton("Konfigurasi",new ImageIcon("src/newbieilmu/rawatinap/image/configure.jpeg"));
    private JButton btnKirim = new JButton ("Kirim",new ImageIcon("src/newbieilmu/rawatinap/image/kirim.jpeg"));
    private Icon gambar = new ImageIcon("src/newbieilmu/rawatinap/image/logo.jpg");
    private JLabel lblGambar = new JLabel(gambar);
    private JLabel lblcekStatus = new JLabel("Cek Status");
    private JLabel lblKirimNo = new JLabel("Kirim ke Nomor");
    private JTextField txtKirimNo = new JTextField();
    private JTextField txtStat = new JTextField();
    private List listProses = new List(); //kelas awt

    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JPanel panel3 = new JPanel();
    private JPanel panel4 = new JPanel();
    
    private JMenuBar bar;
    private JMenu mnuMaster;
    private JMenuItem itemBroadcast,itemAbout;
    //ambil
    String kata      = "";
	String config    = "";
	int    i         = 0;
	int    status    = 0;


public rawatInap()
{
    super("Sistem Informasi Rawat Inap");
    setSize(700,670);
    show();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(d.width/2 - getWidth()/2,d.height/2 - getHeight()/2 );
	setIconImage(getToolkit().getImage("src/newbieilmu/rawatinap/image/kurir.jpeg"));
	
	//menu
	bar = new JMenuBar();
	setJMenuBar(bar);
	
	mnuMaster = new JMenu("Master"); 
		itemBroadcast = new JMenuItem("Broadcast");
		mnuMaster.add(itemBroadcast);
		itemBroadcast.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					if(ae.getSource()==itemBroadcast)
					{
						new Broadcast();
					}
				}
			}
		
		);
		
		itemAbout = new JMenuItem("About");
		mnuMaster.add(itemAbout);
		itemAbout.addActionListener(
			new ActionListener()
			 {
			public void actionPerformed(ActionEvent e)
				{

				if (e.getSource()==itemAbout)
					{
						JOptionPane.showMessageDialog(null,"Sistem Rawat Inap Rs.Jiwa \n " +
								"        Copyright(c) 2012/2013 \n  Anugrah Bagus Susilo \n            " +
								"Versi 1.0 \nCodeName : Bandeng Presto \n   Universitas Budi Luhur");
					}
				}

			}
	
	);
		bar.add(mnuMaster);
    panel1.setLayout(new GridLayout(2,1));
    //panel1.add(lblJudul);



	panel2.setLayout(new GridLayout(1,1));
    panel2.add(lblGambar);

    panel3.setLayout(new GridLayout(2,3,10,20));
    panel3.setBorder(BorderFactory.createMatteBorder(2, 4, 2, 4,Color.LIGHT_GRAY));
    panel3.add(lblcekStatus);
    panel3.add(listProses);
    panel3.add(lblKirimNo);
    panel3.add(txtKirimNo);
    panel3.add(btnClear);
    panel3.add(btnConf);
    panel3.add(btnTestConf);
    panel3.add(btnKirim);

    panel4.setLayout(new BorderLayout());
    panel4.add(panel1,BorderLayout.NORTH);
    panel4.add(panel2,BorderLayout.CENTER);
    panel4.add(panel3,BorderLayout.SOUTH);
    konten.add(panel4);

    //ambil
    	cb_pilihBaud.setSelectedItem("19200");
    	cb_pilihDataBits.setSelectedItem("8");
    	cb_pilihParity.setSelectedItem("None");
    	cb_pilihFlow.setSelectedItem("None");
    	cb_pilihStop.setSelectedItem("1");

    //ambil
    	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
    	while (portList.hasMoreElements()) {
      		CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
      		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
      			cb_nama_pot.addItem(portId.getName());
      		}
    	}
    //ambil
    	portName= cb_nama_pot.getSelectedItem().toString();
   		config  = cb_nama_pot.getSelectedItem().toString()+"~"+cb_pilihBaud.getSelectedItem()+
    			  "~"+cb_pilihDataBits.getSelectedItem()+"~"+cb_pilihParity.getSelectedItem()+"~"+
    			  cb_pilihFlow.getSelectedItem()+"~"+cb_pilihStop.getSelectedItem();

    btnConf.addActionListener(this);
	btnTestConf.addActionListener(this);
	btnKirim.addActionListener(this);
	btnClear.addActionListener(this);

}//Akhir Konstruktor

	//ambil
	public void actionPerformed(ActionEvent act)
{
    if(act.getSource() == btnClear)
    {
        Kosong();
    }
    else if(act.getSource() == btnConf)
    {
     	Configure();
    }
    else if(act.getSource() == btnTestConf)
    {
    if(status == 0)
    	setTerminal();
    else
    	prosesTutup();
    }
    else if(act.getSource() == btnKirim)
    {
       prosesKirimSms(txtKirimNo.getText().trim(), txtStat.getText().trim().toLowerCase());
    }
}

	//ambil
	public void Kosong()
{
		txtKirimNo.setText("");
		txtStat.setText("");
}

  	//ambil
  	int bufferOffset = 0;
  	byte[] bacaBuffer = new byte[100000];
  	int n;

  	//ambil
  	/*
	 * Melakukan pendeteksian respon dari terminal secara otomatis
	 */
	public void serialEvent(SerialPortEvent event) {
	   try {
	     // Apabila ada respons dari terminal, lakukan pembacaan
	     while ( (n = input.available()) > 0)
	     {
	       n = input.read(bacaBuffer, bufferOffset, n);
	       bufferOffset += n;
		        // Jika ada respons "\15" (Line Feed Carriage Return),
	       if ( (bacaBuffer[bufferOffset - 1] == 10) &&
	           (bacaBuffer[bufferOffset - 2] == 13))
	           {
	        		 String buffer = new String(bacaBuffer, 0, bufferOffset - 2);
	       		 // Berikan ke methode terimaAT
	        		 terimaAT(buffer);
	        		 bufferOffset = 0;
	      		} // Akhir if
	     } // Akhir while
	   } // Akhir try
	   catch (IOException e)
	   {

	   }
	} // Akhir methode serialEvent

	//ambil
	private String[] hasil;
  	private int      Index;
  	private int      panjangPDU;
  	private int      PDU = 0;
  	private String   respons;
  	private StringTokenizer st;

	//ambil
	/*
	 * Melakukan respon terhadap terminal dari respon yang diterima
	 * dari method serialEvent
	 */
  	private void terimaAT(String buffer) {
    	// Menguraikan buffer berdasarkan karakter CRLF
    	st = new StringTokenizer(buffer, "\r\n");

    	while (st.hasMoreTokens()) {
      		// mengambil token yang ada pada obyek
      		respons = st.nextToken();
      		// Cetak respon ke layar
      		System.out.println ("respons "+respons);
      		listProses.add(respons, ++i);
      		listProses.select(i);

      		// MelistProses respon yang diterima
      		try
      		{
        		// Jika Ada Telepon yang Masuk
        		if (respons.startsWith("RING"))
        		{
        	  		kirimAT("ATH0" + "\15", 100); // Diputuskan
        		} // Akhir if "RING"

     		  	// Jika ada Pesan Baru yang Masuk
        		else if (respons.startsWith("+CMTI:"))
        		{
          			Pattern pattern = Pattern.compile(",");
          			hasil = pattern.split(respons.trim());
          			Index = Integer.parseInt(hasil[1].trim());
          			System.out.println ("Index "+Index);
          		    kirimAT("AT+CMGR=" + Index + "\15", 1250); // Baca Pesan Baru yang Masuk
        		} // Akhir if "+CMTI:"

        		// Jika ada Pesan Baru Yang dibaca
        		else if (respons.startsWith("+CMGR:")) {
          			PDU = 1;
        		} // Akhir if "+CMGR:"

        		// Membaca Pesan Indox yang belum dibaca
        		else if (respons.startsWith("+CMGL")) {
          			Pattern pattern = Pattern.compile(":");
          			hasil = pattern.split(respons.trim());
          			pattern = Pattern.compile(",");
          			System.out.println ("hasil[1] "+hasil[1]+"Index "+hasil[0]);
          			hasil = pattern.split(hasil[1].trim());
          			Index = Integer.parseInt(hasil[0].trim());
          			PDU = 1;

        		} // Akhir if "+CMGL"
        		else if (PDU == 1) {
          			prosesTerimaSms(Index, respons.trim());
          			PDU = 0;
        		}
        		else {}
      		}
      		catch (Exception e) {} // Akhir while
    	}
  	}

	//ambil
	/*
	 * Proses terima sms dari respon terimaAT
	 */
  	private void prosesTerimaSms(int Index, String Pdu) {
    	try {
      		// Rubah dari format PDU menjadi Format Teks
      		PduTerimaSms(Pdu);
    	} // Akhir try
    	catch (Exception e) {}
    	// Hapus Pesan yang Telah dibaca
    	kirimAT("AT+CMGD=" + Index + "\15", 1250);

  	}

  	//ambil
  	/*
	 * Proses kirim sms terminal ke hp
	 */
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

	      		JOptionPane.showMessageDialog(rawatInap.this,
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

  	//ambil
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


  	//ambil
  	/*
	 *	Konversi terima sms dari terminal dalam format PDU(GSM)
	 *	kedalam format komputer(ASCII)
	 */
  	private void PduTerimaSms(String smspdu) {
    	int i = 0;
    	try {
      		// Mengambil nilai panjang informasi SMSC
      		infoSmsc = smspdu.substring(i, 2);
      		System.out.println ("infoSmsc  : "+infoSmsc);

      		nilaiSmsc = Integer.parseInt(infoSmsc, 16);
      		System.out.println ("nilaiSmsc : "+nilaiSmsc);

      		// format nomor dan nomor  SMSC dibuang
      		i = i + 4;
      		nomorSmsc = i + (nilaiSmsc * 2) - 2;
      		System.out.println ("i= "+i);
      		System.out.println ("nomorSmsc : "+nomorSmsc);

      		// Nilai PDU Type dibuang
      		i = nomorSmsc + 2;
      		// Mengambil Panjang Nomor Telepon Pengirim
      		System.out.println ("i= "+i);
      		panjangNotlp = smspdu.substring(i, i + 2);
      		nilaiPanjangNotlp = Integer.parseInt(panjangNotlp, 16);
      		System.out.println ("panjangNoTlp : "+panjangNotlp);
      		System.out.println ("nilaiPanjangNoTlp : "+nilaiPanjangNotlp);

      		// format nomor pengirim dibuang
      		i = i + 4;
      		nilaiNotlp = i + nilaiPanjangNotlp + nilaiPanjangNotlp % 2;
      		System.out.println ("nilaiNoTlp : "+nilaiNotlp);

      		// Nomor telepon pengirim
      		Notlp = smspdu.substring(i, nilaiNotlp);
      		dapatNotlp = balikKarakter(Notlp);
      		i = nilaiNotlp;
      		System.out.println ("NoTlp : "+Notlp);
      		System.out.println ("dapatNotlp : "+dapatNotlp);

      		// Nilai PID, DCS, dan SCTS dibuang
      		i = i + 18;
      		// Mengambil Panjang Pesan SMS
      		panjangPesan = smspdu.substring(i, i + 2);
      		nilaiPanjangPesan = Integer.parseInt(panjangPesan, 16);
      		i = i + 2;
      		System.out.println ("PanjangPesan : "+panjangPesan);
      		System.out.println ("nilaiPanjangPesan : "+nilaiPanjangPesan);

      		pesanPDU = smspdu.substring(i, smspdu.length());
      		pesan = delapanKeTujuhBit(pesanPDU, nilaiPanjangPesan);
      		System.out.println ("pesanPDU : "+pesanPDU);
      		System.out.println ("pesan : "+pesan);

	    	String dapetPesan = pesan.toLowerCase();
	    	System.out.println ("dapetPesan : "+dapetPesan);

	    	// Jika nomor telepon pengirim diakhiri dengan "F"
	    	if (dapatNotlp.endsWith("F") || dapatNotlp.endsWith("f")) {
	      		// Buang karakter "F"
	      		dapatNotlp = dapatNotlp.substring(0, dapatNotlp.length() - 1);
	    	}

	    	// Cetak ke Layar
	    	listProses.select(i);
	    	txtKirimNo.setText(dapatNotlp);
	    	//txtStat.setText(dapetPesan);

	    	//kirim parameter ke ubah posisi mobil
	    	posisi2(dapetPesan);
    	}
    	catch (Exception e) {}
  	}

  	//ambil
  	private static StringBuffer pesanPDUKirim     = null;
  	private static int          panjangNotlpTujuan= 0;
  	private static int          panjangPesanKirim = 0;
  	private static String       PduPesan          = null;

	//ambil
	/*
	 *	Konversi kirim sms dari program dalam format komputer(ASCII)
	 *	kedalam format PDU(GSM)
	 */
  	
  	
  	
  	
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


  	//ambil
  	private static int          panjangKarakter= 0;
  	private static StringBuffer stringBuffer   = null;

  	//ambil

	/*
	 *	Proses membalikkan karakter string
	 *	misal : indra -> ardni
	 *	 */
  	private static String balikKarakter(String karakter) {
    	panjangKarakter = karakter.length();
    	stringBuffer = new StringBuffer(panjangKarakter);
    	for (int i = 0; (i + 1) < panjangKarakter; i = i + 2) {
      		stringBuffer.append(karakter.charAt(i + 1));
      		stringBuffer.append(karakter.charAt(i));
    	}
    	return new String(stringBuffer);
  	}

  	//ambil
  	private static char[] hexa;
  	private static char[] karakter;

	/*
	 *	Method merubah string ke dalam bentuk hexa
	 */
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

  	//ambil
  	private static char[] asciiToGsmMap; // ASCII ==> GSM

	/*
	 *	Method merubah format Komputer(ASCII tujuh bit)
	 *	kedalam format PDU(GSM delapan bit)
	 *
	 */

	 //ambil
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
//        		System.out.println ("1<<7 "+1<<7);
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

  	//ambil
  	private static char[] gsmToAsciiMap; // GSM ==> ASCII

	/*
	 *	Method merubah format PDU(GSM delapan bit)
	 *	kedalam format Komputer(ASCII tujuh bit)
	 *	!
	 */

	 //ambil
	 private static String delapanKeTujuhBit(String pesan, int msglen) {
    	int i, o, r = 0, rlen = 0, olen = 0, charcnt = 0;
    	StringBuffer msg = new StringBuffer(160);
    	int pesanlen = pesan.length();
    	String ostr;
    	char c;

    	// pengulangan hingga nilai terpenuhi
    	// i + 1 < pesanlen dan charcnt < msglen
    	for (i = 0; ( (i + 1) < pesanlen) && (charcnt < msglen); i = i + 2) {
      		// mengambil dua digit Hexadesimal
      		ostr = pesan.substring(i, i + 2);
      		o = Integer.parseInt(ostr, 16);
      		// berikan nilai olen = 8
      		olen = 8;

      		// geser posisi semua bit ke kiri sebanyak rlen bit
      		o <<= rlen;
      		o |= r; // berikan sisa bit dari o ke r
      		olen += rlen; // olen = olen + rlen

      		c = (char) (o & 127); // mendapatkan nilai o menjadi 7 bit
      		o >>>= 7; // geser posis bit ke kanan sebanyak 7 bit
      		olen -= 7;

      		r = o; // menaruh sisa bit dari o ke r.
      		rlen = olen;

      		c = gsmToAsciiMap[c]; // rubah ke Text (kode ASCII)
      		msg.append(c); // tambahkan ke msg
      		charcnt++; // nilai charcnt ditambahkan 1

      		// jika rlen >= 7
      		if (rlen >= 7) {
        		c = (char) (r & 127);
        		r >>>= 7;
        		rlen -= 7;
        		msg.append(c);
        		charcnt++;
      		}
    	} // Akhir for
    	if ( (rlen > 0) && (charcnt < msglen)) {
      		msg.append( (char) r);
    	}
    	return msg.toString();
  	}

	//ambil
	static {
    	final int lastindex = 255;
    	gsmToAsciiMap = new char[lastindex + 1];
    	asciiToGsmMap = new char[lastindex + 1];
    	int i;

    	for (i = 0; i <= lastindex; i++) {
      		gsmToAsciiMap[i] = asciiToGsmMap[i] = (char) i;
    	}
  	}

  	//ambil
  	private final String pilihBaud[] = {"2400","4800","9600","19200"},
  	pilihDataBits[] = {"5","6","7","8"},
  	pilihParity[]   = {"Even","Odd","None","Mark","Space"},
  	pilihFlow[]     = {"Xoff/Xon","Hardware","None"},
  	pilihStop[]     = {"1","1,5","2"};

  	//ambil
  	private JComboBox cb_pilihBaud = new JComboBox(pilihBaud),
  	cb_pilihDataBits = new JComboBox(pilihDataBits),
  	cb_pilihParity   = new JComboBox(pilihParity),
  	cb_pilihFlow     = new JComboBox(pilihFlow),
  	cb_pilihStop     = new JComboBox(pilihStop),
  	cb_nama_pot      = new JComboBox();

  	//ambil
  	/*
	 *	Method untuk Konfigurasi terminal
	 */
  	private void Configure() {
  		JLabel lbl1=new JLabel("Port"),
  		lbl2=new JLabel("Baud Rate"),
  		lbl3=new JLabel("Data Bits"),
  		lbl4=new JLabel("Parity"),
  		lbl5=new JLabel("Stop Bits"),
  		lbl6=new JLabel("Flow Control");

  		JPanel p=new JPanel(new GridLayout(6,2));
  		p.add(lbl1); p.add(cb_nama_pot);
  		p.add(lbl2); p.add(cb_pilihBaud);
  		p.add(lbl3); p.add(cb_pilihDataBits);
  		p.add(lbl4); p.add(cb_pilihParity);
  		p.add(lbl5); p.add(cb_pilihStop);
  		p.add(lbl6); p.add(cb_pilihFlow);

  		Object opt[]={"OK","BATAL"};
  		int ok=JOptionPane.showOptionDialog(this,p,"Configure",
  		JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,opt,opt[0]);
  		if(ok == 0) {
  			portName=cb_nama_pot.getSelectedItem().toString();
  			nilaiBaud=Integer.parseInt(cb_pilihBaud.getSelectedItem().toString());
  			if(cb_pilihDataBits.getSelectedIndex() == 0)
  				nilaiData=SerialPort.DATABITS_5;
  			else if(cb_pilihDataBits.getSelectedIndex() == 1)
  				nilaiData=SerialPort.DATABITS_6;
  			else if(cb_pilihDataBits.getSelectedIndex() == 2)
  				nilaiData=SerialPort.DATABITS_7;
  			else if(cb_pilihDataBits.getSelectedIndex() == 3)
  				nilaiData=SerialPort.DATABITS_8;

  			String Parity=cb_pilihParity.getSelectedItem().toString().toUpperCase();
    		if (Parity.equals("EVEN"))
      			nilaiParity = SerialPort.PARITY_EVEN;
    		else if (Parity.equals("ODD"))
      			nilaiParity = SerialPort.PARITY_ODD;
    		else if (Parity.equals("NONE"))
      			nilaiParity = SerialPort.PARITY_NONE;
    		else if (Parity.equals("MARK"))
      			nilaiParity = SerialPort.PARITY_MARK;
      		else if (Parity.equals("SPACE"))
      			nilaiParity = SerialPort.PARITY_SPACE;

  			String Stop=cb_pilihStop.getSelectedItem().toString();
    		if (Stop.equals("1"))
      			nilaiStop = SerialPort.STOPBITS_1;
    		else if (Stop.equals("1,5"))
      			nilaiStop = SerialPort.STOPBITS_1_5;
    		else if (Stop.equals("2"))
      			nilaiStop = SerialPort.STOPBITS_2;

      		String Flow=cb_pilihStop.getSelectedItem().toString().toUpperCase();
    		if (Flow.equals("NONE")) {
      			nilaiFlow = SerialPort.FLOWCONTROL_NONE;
    		}
    		else if (Flow.equals("HARDWARE")) {
      			nilaiFlow = SerialPort.FLOWCONTROL_RTSCTS_IN |
          		SerialPort.FLOWCONTROL_RTSCTS_OUT;
    		}
    		else if (Flow.equals("XOFF/XON")) {
      			nilaiFlow = SerialPort.FLOWCONTROL_XONXOFF_IN |
          		SerialPort.FLOWCONTROL_XONXOFF_OUT;
    		}
    		config=cb_nama_pot.getSelectedItem().toString()+"~"+cb_pilihBaud.getSelectedItem()+
    		"~"+cb_pilihDataBits.getSelectedItem()+"~"+cb_pilihParity.getSelectedItem()+"~"+
    		cb_pilihFlow.getSelectedItem()+"~"+cb_pilihStop.getSelectedItem();
  		}
  		else {
  			String data[]=config.split("~");

    		cb_nama_pot.setSelectedItem(data[0]);
    		cb_pilihBaud.setSelectedItem(data[1]);
    		cb_pilihDataBits.setSelectedItem(data[2]);
    		cb_pilihParity.setSelectedItem(data[3]);
    		cb_pilihFlow.setSelectedItem(data[4]);
    		cb_pilihStop.setSelectedItem(data[5]);
  		}
  	}
  	
  	public void posisi2(String getKata) {
		//mengambil isi sms tanpa karakter aneh
		String data = getKata.substring(8); 
  		String[] data1 = data.split(" ");
		if(data1[0].equals("cari"))
		{
			cariRuang(data1[1]);
		}
		else if(data1[0].equals("cek"))
		{
			cekDokter(data1[1]);
		}/*
		else if(data[0].equals("cari"))
		{
			cariRuang(data[1]);
		}
		else if(data[0].equals("info"))
		{
			infoBiaya(data[1]);
		}
		else if(data[0].equals("jumlah")){
			jumlahBayar(data[1]);
		}*/
		}
  	//ambil
  	/*
	 *	Method untuk menginisialisasi posisi mobil
	 */
	public void posisi(String isisms) {
		//mengambil isi sms tanpa karakter aneh
		String[] data = isisms.split("#");
		if(data[0].equals("reg"))
		{
			regDaftar(data[1]);
		}
		else if(data[0].equals("cek"))
		{
			cekDokter(data[1]);
		}
		else if(data[0].equals("cari"))
		{
			cariRuang(data[1]);
		}
		else if(data[0].equals("info"))
		{
			infoBiaya(data[1]);
		}
		else if(data[0].equals("jumlah")){
			jumlahBayar(data[1]);
		}
	
	}
	public void regDaftar(String NoDaftar)
	{
		try
		{
		koneksi = objKoneksi.open_a_Connection();
		state = koneksi.createStatement();
		sql ="select * from daftar where NoDaftar = '"+NoDaftar+"'";
		rs = state.executeQuery(sql);
		System.out.println("sql "+sql);
			if(rs.next())
			{
			pesan = //"NoDaftar : " + rs.getString(1)+ 
				"NoPasien : " + rs.getString(2)+"Tgl_Daftar: " + rs.getString(3) +"Tgl_Masuk : " + rs.getString(4)+"KdDokter : " + rs.getString(5)+"KdRuang : " + rs.getString(6);
			prosesKirimSms(dapatNotlp.trim(),pesan);
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik" + pesan);
			}
		state.close();
		koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
		
	//ambil
	public void cekDokter(String KdDokter)
	{
		try
		{
		koneksi = objKoneksi.open_a_Connection();
		state = koneksi.createStatement();
		sql ="select * from dokter where KdDokter = '"+KdDokter+"'";
		rs = state.executeQuery(sql);
		System.out.println("sql "+sql);
			if(rs.next())
			{
			pesan = //"KdDokter : " + rs.getString(2)+ 
			"NmDokter : " + rs.getString(2)+"Jns_Kel: " + rs.getString(3) +"Alamat: " + rs.getString(4);
			prosesKirimSms(dapatNotlp.trim(),pesan);
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik" + pesan);
			}
		state.close();
		koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
	
	public void cariRuang(String KdRuang)
	{
		try
		{
		koneksi = objKoneksi.open_a_Connection();
		state = koneksi.createStatement();
		sql ="select * from ruang where KdRuang = '"+KdRuang+"'";
		rs = state.executeQuery(sql);
		System.out.println("sql "+sql);
			if(rs.next())
			{
			pesan = //"KdRuang : " + rs.getString(2)+
				"NmRuang : " + rs.getString(2);
			prosesKirimSms(dapatNotlp.trim(),pesan);
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik" + pesan);
			}
		state.close();
		koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
	
	public void infoBiaya(String KdBiaya)
	{
		try
		{
		koneksi = objKoneksi.open_a_Connection();
		state = koneksi.createStatement();
		sql ="select * from biaya where KdBiaya = '"+KdBiaya+"'";
		rs = state.executeQuery(sql);
		System.out.println("sql "+sql);
			if(rs.next())
			{
			pesan = //"KdBiaya : " + rs.getString(2)+
				"NoDaftar : " + rs.getString(2)+"Jmlh_Harga : " + rs.getString(3);
			prosesKirimSms(dapatNotlp.trim(),pesan);
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik" + pesan);
			}
		state.close();
		koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
	
	public void jumlahBayar(String KdKwitansi)
	{
		try
		{
		koneksi = objKoneksi.open_a_Connection();
		state = koneksi.createStatement();
		sql ="select * from dokter where KdDokter = '"+KdKwitansi+"'";
		rs = state.executeQuery(sql);
		System.out.println("sql "+sql);
			if(rs.next())
			{
			pesan = //"KdKwitansi : " + rs.getString(2)+
				"Tgl_Bayar : " + rs.getString(2)+"NoKeluar : " + rs.getString(3) +"TotalBayar : " + rs.getString(4);
			prosesKirimSms(dapatNotlp.trim(),pesan);
			JOptionPane.showMessageDialog(this,"Sukses Mengirim Balik" + pesan);
			}
		state.close();
		koneksi.close();
		}
		catch(SQLException ex)
		{
		System.out.println(ex);
		}
	}
	//ambil
	SerialPort         port    = null;
  	Enumeration        portList= null;
  	CommPortIdentifier portId  = null;
  	InputStream        input;
  	OutputStream       output;
  	String portName = null; // Nama Port
  	int nilaiBaud   = 19200; // Nilai Baud Rate
  	int nilaiData   = SerialPort.DATABITS_8; // Nilai DATABITS
  	int nilaiStop   = SerialPort.STOPBITS_1; // Nilai STOPBITS
  	int nilaiParity = SerialPort.PARITY_NONE; // Nilai PARITY
  	int nilaiFlow   = SerialPort.FLOWCONTROL_NONE; // Nilai FLOWCONTROL

  	//ambil
  	 /*
	 *	Method untuk membuka dan mengeset terminal
	 */
  	public void setTerminal() {
    	// Cetak pesan ke layar
    	listProses.add("Server Sedang melakukan pencarian Port", ++i);
    	// Mencari daftar port-port yang tersedia
    	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
    	while (portList.hasMoreElements()) {
      		// Mengambil nilai-nilai port yang ditemukan
      		CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
      		// Hanya Port Serial yang diambil
      		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        		// Buka port berdasarkan nama port yang telah ditentukan (COM1)
        		if (portId.getName().equals(portName)) {
          			try {
            			port = (SerialPort) portId.open("SMS", 5000);
            			// Cetak pesan ke layar
            			listProses.add("Server berhasil membuka Port : " + portName, ++i);
            			listProses.select(i);

          			} //Akhir try open port
          			catch (PortInUseException piue) {
            			listProses.add("Port : " + portName + " Sedang digunakan", ++i);
            			listProses.add("Penyambungan ke Terminal Gagal .........", ++i);
            			listProses.add("Terjadi kesalahan pada : " + piue, ++i);
            			listProses.select(i);
          			} //Akhir catch
        		} //Akhir if port getName
      		} // Akhir if port getPortType
    	} // Akhir while

    	// Membuka input dan output Stream pada Port
    	try {
      		output = port.getOutputStream();
      		input = port.getInputStream();
    	} // Akhir try stream
    	catch (IOException ioe) {
      		listProses.add("Gagal membuka Stream", ++i);
      		listProses.add("Terjadi kesalahan pada : " + ioe, ++i);
      		listProses.select(i);
    	} //Akhir catch
    	// Mengatur Konfigurasi dari Serial Port
    	try {
      		port.setSerialPortParams(nilaiBaud, nilaiData, nilaiStop, nilaiParity);
      		port.setFlowControlMode(nilaiFlow);
      		// Menerima pemberitahuan jika ada data pada terminal
      		port.notifyOnDataAvailable(true);

      		// Cetak pesan ke layar
      		listProses.add("Server Melakukan Hubungan ke Port : " + portName, ++i);
      		listProses.add("Server Berhasil Tehubung ke Port : " + portName, ++i);
      		listProses.add("Server Sedang melakukan Pengaturan Terminal", ++i);
      		listProses.add("Tunggu Sebentar .....", ++i);
      		listProses.select(i);

      		// Melakukan pengatur TERMINAL
      		kirimAT("AT" + "\15", 1250); // Apakah terminal telah siap
      		kirimAT("AT+CMGF=0" + "\15", 1250); // Menetapkan Format PDU Mode
      		kirimAT("AT+CSCS=\"GSM\"" + "\15", 1250); // Menetapkan Encoding
      		kirimAT("AT+CPMS=\"ME\"" + "\15", 1250); // Mendengarkan pesan secara Otomatis
      		kirimAT("AT+CNMI=2,3,2,1,0" + "\15", 1250); // Mendengarkan pesan secara Otomatis
      		kirimAT("AT+CMGL=0" + "\15", 1250); // Membaca pesan yang belum dibaca yang ada di dalam Inbox

      		// Status server Terminal telah tersambung
      		status++;
    	} //Akhir try serial port params
    	catch (UnsupportedCommOperationException ucoe) {
      		listProses.add("Pengaturan Data Serial Port Gagal", ++i);
      		listProses.add("Kesalahan terjadi pada : " + ucoe, ++i);
      		listProses.select(i);
    	}
    	// Menambahkan Event Listener pada Serial Port
    	try {
      		port.addEventListener(this);
    	} //Akhir try addEvenListener
    	catch (TooManyListenersException tmle) {
      		listProses.add("Terjadi kesalahan pada : " + tmle, ++i);
      		listProses.select(i);
    	}
	}

	//ambil
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

  	//ambil
  	/*
	 *	Method menutup koneksi dengan terminal
	 */
  	private void prosesTutup() {
  		try {
  			port.close();
  			status = 0;
  			listProses.clear();
  		}
  		catch(Exception e) {
  			e.printStackTrace();
  		}
  	}

	//ambil
	/*
  	 *	Method yang ngecek no.telp tujuan
  	 */
  	public boolean cekNomor() {
  		if(txtKirimNo.getText().trim().equals("") || txtStat.getText().trim().equals("")) {
  			JOptionPane.showMessageDialog(rawatInap.this,
  									"Isi pesan atau nomor hp tujuan terlebih dahulu",
  									"Warning", JOptionPane.WARNING_MESSAGE);
  			return false;
  		}
  		else if(!txtKirimNo.getText().matches("[0-9]*")) {
  			JOptionPane.showMessageDialog(rawatInap.this,
  									"nomor hp tujuan belom bener tuch!",
  									"Warning", JOptionPane.WARNING_MESSAGE);
  			return false;
  		}
  		else
  			return true;
  	}
  	

public static void main(String[] args)
{
    try{
        	UIManager.setLookAndFeel(new McWinLookAndFeel());
        }
	catch (UnsupportedLookAndFeelException e){
		}

    new rawatInap();
}


}


