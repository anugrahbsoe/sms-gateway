import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.wireless.messaging.*;
import java.io.IOException;

public class SMSCENTERForm extends Form implements CommandListener,Runnable
{
private Display display;
private Command cmBack , cmKirim;
private SMSCENTER midlet;
private String msg,adr,pesan;
private TextField txtIDDokter;
private TextField txtNoSupplier;
private int Index;
private String port;


    public SMSCENTERForm(SMSCENTER midlet,Display display , int choice,String smsPort)
    {
	super("SMS CENTER RAWAT INAP");
	this.display = display;
	this.midlet = midlet;
	this.Index = choice;
	switch(choice)
	{

	case 0 :
			append ("Cek Dokter");
			txtKdRuang = new TextField("ID Dokter", "" , 160 ,TextField.ANY);
			append(txtKdRuang);
			break;
	case 1 :
			append ("Info Supplier");
			txtKdDokter = new TextField("No Supplier", "" , 160 ,TextField.ANY);
			append(txtKdDokter);
			break;
	}

	cmBack = new Command("Kembali" ,Command.BACK,1);
	addCommand(cmBack);
	cmKirim = new Command("Kirim",Command.OK,1);
	addCommand(cmKirim);
	setCommandListener(this);

	}

	public void commandAction(Command c,Displayable s)
	{
		if(c == cmBack)
		{
			display.setCurrent(midlet.list);

		}
		else if(c == cmKirim)
		{
		switch(Index)
		{
		case 0:
				pesan = "Cek " + txtKdRuang.getString();
				break;
		case 1:
				pesan = "Info " + txtKdDokter.getString();
				break;
		}
		this.adr = "sms://" + "6289637793533" + ":" + "500";
		new Thread(this).start();
		}
	}

		public void run()
		{
			MessageConnection smsconn = null;
			try
			{
			smsconn = (MessageConnection)Connector.open(adr);
			TextMessage txtmessage =
			(TextMessage)smsconn.newMessage(MessageConnection.TEXT_MESSAGE);
			txtmessage.setAddress(adr);
			txtmessage.setPayloadText(pesan.trim());
			smsconn.send(txtmessage);
			Alert alert = new Alert("Info","Pesan Terkirim",null,AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			display.setCurrent(alert);
			}
			catch(Throwable t)
			{
			System.out.println("error : " + t.toString());
			Alert alert = new Alert("Error" , "Pesan Gagal Terkirim",null,AlertType.ERROR);
			alert.setTimeout(Alert.FOREVER);
			display.setCurrent(alert);

			}
			if(smsconn != null)
			{
				try
				{
					smsconn.close();
				}
				catch(IOException ioe)
				{

				}
			}
		}
}
