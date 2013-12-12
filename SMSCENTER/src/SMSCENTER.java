

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class SMSCENTER extends MIDlet implements CommandListener
{
	Display display;
	SMSCENTERForm form;
	List list;
	Command cmExit;

	public SMSCENTER()
	{
		display = Display.getDisplay(this);
	}

	public void startApp()
	{
		list = new List("SMS CENTER RAWAT INAP",List.IMPLICIT);
		list.append("Cek Dokter",null);
		list.append("Info Supplier",null);
		//list.append("Lihat Obat",null);

		cmExit = new Command("Keluar",Command.EXIT,0);

		list.addCommand(cmExit);
		list.setCommandListener(this);
		display.setCurrent(list);
	}

	public void pauseApp()
	{
	}

	public void destroyApp(boolean unconditional)
	{
	}

	public void exitMIDlet()
	{
		destroyApp(false);
		notifyDestroyed();
	}

	public void commandAction(Command c,Displayable s)
	{
		if(c == List.SELECT_COMMAND)
		{
			String smsPort = getAppProperty("SMS-Port");
			switch(list.getSelectedIndex())
			{
				case 0 :
					form = new SMSCENTERForm(this,display,0,smsPort);
					display.setCurrent(form);
					break;
				case 1 :
					form = new SMSCENTERForm(this,display,1,smsPort);
					display.setCurrent(form);
					break;
			}
		}
		else if(c == cmExit)
		{
			exitMIDlet();
		}

	}

}

