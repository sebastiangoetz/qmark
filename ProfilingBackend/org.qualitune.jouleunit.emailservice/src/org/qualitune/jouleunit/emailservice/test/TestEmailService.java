package org.qualitune.jouleunit.emailservice.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestEmailService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Socket kkSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		String serverIP = "localhost";
		int serverPort = 5725;

		System.out.println("Try to connect to Email Service ...");

		try {
			kkSocket = new Socket(serverIP, serverPort);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					kkSocket.getInputStream()));
		}

		catch (UnknownHostException e) {
			System.out.println("Cannot connect to host " + serverIP
					+ ". Are you sure that your settings are correct?");
			return;
		}

		catch (IOException e) {
			System.out.println("Couldn't get I/O for the connection to "
					+ serverIP
					+ ". Are you sure that your settings are correct?");
			return;
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String fromServer;

		try {
			out.println(1);

			while ((fromServer = in.readLine()) != null) {
				System.out.println(fromServer);

				if (fromServer.equals("... done") || fromServer.equals("abort")) {
					break;
				}
			}

			in.close();
			out.close();
			stdIn.close();
			kkSocket.close();
		}

		catch (IOException e) {
			System.out
					.println("Connection to remote sever failed. Are you sure that the settings are correct?\nCaused Exception: "
							+ e.getMessage() + ".");
			return;
		}
	}

}
