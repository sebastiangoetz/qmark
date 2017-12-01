package com.switching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Connect to USB Switching Box! Please wait...");
		SwitchBox box = new SwitchBox("COM8");
		if (box.serialAvailable())
			System.out.println("Connected!");
		else
			System.out.println("No Switch Box!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean working = true;
		while (working)
		{
			System.out.print("next command: ");
			String befehl = null;
			try {
				befehl = br.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error trying to read your name!");
				System.exit(1);
			}
			if (befehl != null && befehl.equals("status"))
			{
				//get Status
				Status s = box.getStatus();
				System.out.println("If all the following values are UNKNOWN, means there is a connection error!");
				System.out.println("Tempature: " + s.getTemperature());
				System.out.println("Power: " + s.getPower().toString());
				System.out.println("Current USB Port: " + s.getUsbPort().toString());
				System.out.println("Auto power on: " + s.getAutoPowerOn().toString());
			}
			if (befehl != null && befehl.equals("usb1"))
			{
				//start usb1
				if (box.activateUsb(1))
					System.out.println("USB 1 is online.");
				else
					System.out.println("Activation error.");
			}
			if (befehl != null && befehl.equals("usb2"))
			{
				//start usb2
				if (box.activateUsb(2))
					System.out.println("USB 2 is online.");
				else
					System.out.println("Activation error.");
			}
			if (befehl != null && befehl.equals("usb3"))
			{
				//start usb3
				if (box.activateUsb(3))
					System.out.println("USB 3 is online.");
				else
					System.out.println("Activation error.");
			}
			if (befehl != null && befehl.equals("usb4"))
			{
				//start usb4
				if (box.activateUsb(4))
					System.out.println("USB 4 is online.");
				else
					System.out.println("Activation error.");
			}
			if (befehl != null && befehl.equals("usboff"))
			{
				//start usb4
				if (box.activateUsb(5))
					System.out.println("USB 4 is online.");
				else
					System.out.println("Activation error.");
			}
			if (befehl != null && befehl.equals("start auto power on"))
			{
				//start
				if (box.setAutoPowerOnState(true))
					System.out.println("Auto power on is started");
				else
					System.out.println("Set state error.");
			}
			if (befehl != null && befehl.equals("stop auto power on"))
			{
				//start
				if (box.setAutoPowerOnState(true))
					System.out.println("Auto power on is stopped");
				else
					System.out.println("Set state error.");
			}
			if (befehl != null && befehl.startsWith("help"))
			{
				//help
				System.out.println("Help! Here is a command list:");
				System.out.println("status              - read the Switching Box status");
				System.out.println("usb1                - switch to USB 1 measuring");
				System.out.println("usb2                - switch to USB 2 measuring");
				System.out.println("usb3                - switch to USB 3 measuring");
				System.out.println("usb4                - switch to USB 4 measuring");
				System.out.println("start auto power on - start the auto power on mode");
				System.out.println("stop auto power on  - stop the auto power on mode");
				System.out.println("power on            - set power on");
				System.out.println("power off           - set power off");
				System.out.println("com                 - current connected ComPort");
				System.out.println("reconnect -preferCom- reconnect with a preferred ComPort");
				System.out.println("exit                - exit programm");
			}
			if (befehl != null && befehl.equals("power on"))
			{
				//start
				if (box.powerOn())
					System.out.println("Power on");
				else
					System.out.println("Set state error.");
			}
			if (befehl != null && befehl.equals("com"))
			{
				//start
				if (box.getComPort().equals(""))
					System.out.println("No ComPort available!");
				else
					System.out.println(box.getComPort());
			}
			if (befehl != null && befehl.startsWith("reconnect"))
			{
				//start
				String comPort = "";
				if (befehl.contains("-"))
				{									
					for (int i=befehl.length()-1;i>0;i--)
					{
						if (befehl.substring(i, i+1).equals("-"))
							break;
						else
							comPort = befehl.substring(i, i+1) + comPort;
					}					
				}
				if (box.init(comPort))
					System.out.println("Connected!");
				else
					System.out.println("No Switch Box!");
			}
			if (befehl != null && befehl.equals("power off"))
			{
				//start
				if (box.powerOff())
					System.out.println("Power off");
				else
					System.out.println("Set state error.");
			}
			if (befehl != null && befehl.equals("exit"))
			{
				box.closeConnection();
				System.out.println("Good bye.");
				working = false;
			}			
		}
	}

}
