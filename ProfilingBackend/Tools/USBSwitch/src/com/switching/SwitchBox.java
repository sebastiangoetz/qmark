package com.switching;

import java.util.LinkedList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * @author      Sebastian Werner
 * @version     1.0
 * @since       2013-06-18
 */
public class SwitchBox {

	private SerialPort serialPort;
	
	private String comPort = "";
	
	/**
	 * Current connected ComPort
	 * 
	 * Return the connected ComPort. The ComPort can be saved in the configuration of the external program 
	 * for a faster connection in the next class initialization.
	 * @return for example COM1
	 */
	public String getComPort() {
		return comPort;
	}
	
	/**
	 * Constructor initialize the serial connection.
	 * 
	 * This constructor initialized the serial connection without any information of a preferred ComPort.
	 */
	public SwitchBox()
	{
		this.init(null);
	}
	
	/**
	 * Constructor initialize the serial connection.
	 * 
	 * This constructor initialized the serial connection with a preferred ComPort. 
	 * So the program first test the preferred ComPort. 
	 * @param preferCom = for example "COM1"
	 */
	public SwitchBox(String preferCom)
	{
		this.init(preferCom);
	}
	
	/**
	 * Initialize the Serial Connection
	 * 
	 * Initialize the Serial Connection by testing all available port and searching the port with the right answer. 
	 * He starts with the preferred Port if it is available.
	 * @return true is online <p>false is offline
	 */
	public boolean init(String preferCom)
	{
		this.comPort = "";
		String[] portNames = SerialPortList.getPortNames();
		for (int i=0; i<portNames.length;i++)
		{
			if (portNames[i].equals(preferCom))
			{
				String zw = portNames[0];
				portNames[0] = portNames[i];
				portNames[i] = zw;
				break;
			}
		}
        for(int i = 0; i < portNames.length; i++){
            System.out.println(portNames[i]);
            this.serialPort = new SerialPort(portNames[i]);
            try {             	
                serialPort.openPort();                
                serialPort.setParams(SerialPort.BAUDRATE_9600, 
                                     SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1,
                                     SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
                Thread.sleep(2000);
                serialPort.readBytes();
                Thread.sleep(2000);
                serialPort.writeBytes("O".getBytes());
                byte[] output;
                int counter = 0;
                do
                {
                	Thread.sleep(1000);
                	output = serialPort.readBytes();
                	counter++;
                	if (output == null)
                		output = new byte[0];
                }
                while (output.length <= 1 && counter < 10);
                String s = new String(output);
                if (s.startsWith("SYSTEM_ONLINE_2308742039765243322480674_CODE")) 
                {
                	this.comPort = portNames[i];
                	break;
                }
                else
                {
                	serialPort.closePort();//Close serial port
                	serialPort = null;
                }
            }
            catch (Exception ex) {
                System.out.println(ex);                
                serialPort = null;
            }
        }
        return this.serialAvailable();
	}
	
	/**
	 * Testing the existence of an open serial port.
	 * @return true is online <p>false is offline
	 */
	public boolean serialAvailable()
	{
		if (this.serialPort == null)
			return false;
		else
			return true;			
	}
	
	/**
	 * Status call
	 * 
	 * Read the status of the switch box.
	 * @return all values are UNKNOWN if there is no serial contact.
	 */
	public Status getStatus()
	{
		if (!this.serialAvailable())
		{
			return new Status();
		}
		try
		{
            serialPort.writeString("S");
            Thread.sleep(2000);
            byte[] output;
            List<Byte> zwOutput = new LinkedList<Byte>();
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null)
            	{       
            		for (int i = 0;i<output.length;i++)
            			zwOutput.add(output[i]);
            		Status status = this.testStatusMessage(zwOutput);
            		if (status != null)
            		{
            			//for (int i = 0;i<zwOutput.size(); i++)
            			//	System.out.print(zwOutput.get(i) + " - ");
            			//System.out.println("");
            			return status;
            		}
            	}
            }
            while (counter < 10); 
            //System.out.println("Unten:");
            //for (int i = 0;i<zwOutput.size(); i++)
			//	System.out.print((zwOutput.get(i) & 0xFF) + " - ");
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
		return new Status();
	}
	
	/**
	 * Activate USB Port
	 * 
	 * Activate the USB Port with the defined number.
	 * @param number Number of the USB Port (1-4)
	 * @return 
	 * true = activated<p>
	 * false = not activated, there can be problems with the serial connection or a arduino problem
	 */
	public boolean activateUsb(Integer number)
	{
		if (!this.serialAvailable())
		{
			return false;
		}
		try
		{
            serialPort.writeString(number.toString());
            Thread.sleep(2000);
            byte[] output;
            List<Byte> zwOutput = new LinkedList<Byte>();
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null)
            	{
            		for (int i = 0;i<output.length;i++)
            			zwOutput.add(output[i]);
            		int onlineUsb = this.getOnlineUsb(zwOutput);
            		if (onlineUsb == number)
            		{                			
            			return true;
            		}
            	}
            }
            while (counter < 60);            
        }
        catch (Exception ex) {
            System.out.println(ex);            
        }
		return false;
	}
	
	/**
	 * Deactivate USB Ports.
	 * @return 
	 * true = activated<p>
	 * false = not activated, there can be problems with the serial connection or a arduino problem
	 */
	public boolean deactivateUsb()
	{
		if (!this.serialAvailable())
		{
			return false;
		}
		try
		{
            serialPort.writeString("5");
            Thread.sleep(2000);
            byte[] output;
            List<Byte> zwOutput = new LinkedList<Byte>();
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null)
            	{
            		for (int i = 0;i<output.length;i++)
            			zwOutput.add(output[i]);            		
            		if (zwOutput.size() >= 3 && zwOutput.get(0) == 125)
            		{                			
            			return true;
            		}
            	}
            }
            while (counter < 10);
            return true;
        }
        catch (Exception ex) {
            System.out.println(ex);            
        }
		return false;
	}
	
	/**
	 * Set the auto power on state.
	 * 
	 * If the auto power on state is active, the arduino starts the power after booting.
	 * @param state true = activate <p>false = deactivate
	 * @return 
	 * true = state set was ok<p>
	 * false = state set error, no connection or wrong answer 
	 */
	public boolean setAutoPowerOnState(boolean state)
	{
		if (!this.serialAvailable())
		{
			return false;
		}
		try
		{
			if (state)
				serialPort.writeString("A");
			else
				serialPort.writeString("M");
            Thread.sleep(2000);
            byte[] output;
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null && output.length >= 1)
            	{
            		if (output[0] == 48 && !state || output[0] == 49 && state)
            			return true;
            		else
            			return false;            			
            	}
            }
            while (counter < 60);            
        }
        catch (Exception ex) {
            System.out.println(ex);            
        }
		return false;
	}
	
	/**
	 * Start power.
	 * @return 
	 * true = state set was ok<p>
	 * false = state set error, no connection or wrong answer 
	 */
	public boolean powerOn()
	{
		if (!this.serialAvailable())
		{
			return false;
		}
		try
		{			
			serialPort.writeString("U");			
            Thread.sleep(2000);
            byte[] output;
            List<Byte> zwOutput = new LinkedList<Byte>();
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null)
            	{       
            		for (int i = 0;i<output.length;i++)
            			zwOutput.add(output[i]);            		
            		if (zwOutput.size() >= 2)
            		{
            			if (zwOutput.get(0) == 125 && zwOutput.get(1) == 106)
            				return true;
            			else
            				return false;
            		}
            	}
            }
            while (counter < 60);            
        }
        catch (Exception ex) {
            System.out.println(ex);            
        }
		return false;
	}
	
	/**
	 * Stop power.
	 * @return 
	 * true = state set was ok<p>
	 * false = state set error, no connection or wrong answer 
	 */
	public boolean powerOff()
	{
		if (!this.serialAvailable())
		{
			return false;
		}
		try
		{
			serialPort.writeString("D");
            Thread.sleep(2000);
            byte[] output;
            List<Byte> zwOutput = new LinkedList<Byte>();
            int counter = 0;
            do
            {
            	Thread.sleep(1000);
            	output = serialPort.readBytes();
            	counter++;   
            	if (output != null)
            	{       
            		for (int i = 0;i<output.length;i++)
            			zwOutput.add(output[i]);            		
            		if (zwOutput.size() >= 2)
            		{
            			if (zwOutput.get(0) == 125 && zwOutput.get(1) == 107)
            				return true;
            			else
            				return false;
            		}
            	}
            }
            while (counter < 60);            
        }
        catch (Exception ex) {
            System.out.println(ex);            
        }
		return false;
	}
	
	/**
	 * Close the serial connection.
	 */
	public void closeConnection()
	{
		if (this.serialAvailable())
		{
			try 
			{
				this.serialPort.closePort();
			} 
			catch (SerialPortException e) 
			{
				System.out.println(e); 
			}
		}
	}
	
	/**
	 * Read the current USB port from the byte list.
	 * Byte System must be: 125 , 103 , USB Port number
	 * @param message
	 * @return USB Port number
	 */
	private int getOnlineUsb(List<Byte> message)
	{
		for (int i = 0; i<message.size()-2;i++)
		{
			if (message.get(i) == 125 && message.get(i + 1) == 103)
				return message.get(i+2);
		}
		return -1;
	}
	
	/**
	 * Test byte list and create status information class.
	 * @param message
	 * @return
	 * null = no correct list
	 * Status Class = correct list
	 */
	private Status testStatusMessage(List<Byte> message)
	{
		int startpos = -1;
		for (int i = 0; i<message.size();i++)
		{
			if (message.get(i) == 125)
			{
				startpos = i;
				break;
			}
		}
		if (startpos == -1)
			return null;
		if (message.size() - startpos >= 12)
		{
			//es ist lang genug jetzt infos auslesen
			USBPort port = USBPort.UNKNOWN;
			Power power = Power.UNKNOWN;
			Power powerAutoOn = Power.UNKNOWN;
			int temp = 0;
			for (int i = startpos + 1; i<message.size();i++)
			{
				if (message.get(i) == 102 && message.size() > i+1)
				{
					i++;
					port = USBPort.values()[message.get(i)];
				}
				if (message.get(i) == 100 && message.size() > i+1)
				{
					i++;
					power = Power.values()[message.get(i)];
				}
				if (message.get(i) == 108 && message.size() > i+1)
				{
					i++;
					powerAutoOn = Power.values()[message.get(i)];
				}
				if (message.get(i) == 105 && message.size() > i+2)
				{
					i++;
					byte first = message.get(i);
					i++;
					byte second = message.get(i);
					temp = ( second << 8 | first );
				}
			}
			Status status = new Status();
			status.setPower(power);
			status.setUsbPort(port);
			status.setAutoPowerOn(powerAutoOn);
			status.setTemperature(temp);
			return status;
		}
		else
			return null;
	}
	
}
