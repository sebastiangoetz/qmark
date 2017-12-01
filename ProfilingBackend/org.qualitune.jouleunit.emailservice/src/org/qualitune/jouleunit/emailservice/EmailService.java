package org.qualitune.jouleunit.emailservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

	/** List of email accounts supported. */
	private enum Account {
		Chuck, Tiffany
	}

	/** {@link TimerTask} sending emails from Chuck to Tiffany. */
	private class EmailTask extends TimerTask {

		private boolean isCancelled = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			if (!isCancelled)
				sendMail(
						Account.Tiffany,
						Account.Chuck,
						"Lorem ipsum!",
						"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget accumsan sapien, in tristique nisi. Nam aliquet justo ac nunc aliquet accumsan. Suspendisse imperdiet a libero eget pharetra. Fusce non dolor eu libero mattis porta. Etiam faucibus rhoncus purus at hendrerit. Cras blandit tristique blandit. Proin congue, felis non sodales auctor, justo orci laoreet nisi, eu mollis mauris augue vitae risus. Donec eu tincidunt eros. Sed ultricies congue gravida. Duis placerat, neque vel ultricies viverra, est risus suscipit purus, ut gravida lectus felis malesuada lacus. Ut rhoncus lectus est, vel vulputate sem eleifend in. Nulla fermentum feugiat purus, id commodo mauris venenatis ac. Maecenas ac libero purus. Maecenas et ligula magna. Pellentesque ipsum elit, tristique lacinia varius nec, pretium sollicitudin felis.\n\nVivamus sollicitudin quam sit amet venenatis ornare. Maecenas ornare a lectus eget viverra. Quisque varius ullamcorper dolor, id laoreet ante. Aliquam ut risus a sapien consequat tristique. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed ornare tellus sapien, at sodales ipsum venenatis eget. Donec in lorem sed nisi laoreet pellentesque eu eget tellus. Ut eget ligula quis quam placerat tempor non vel ipsum.\n\nSed accumsan risus et ligula posuere commodo. Praesent placerat neque suscipit purus mattis, ac cursus neque pharetra. Donec vitae dapibus mi. Donec id ornare dui, lobortis consectetur nibh. Quisque tristique accumsan dui, in vehicula nibh aliquam quis. Sed rhoncus sed tellus ut malesuada. Praesent venenatis vel sem non hendrerit. Quisque lacinia nibh varius erat vulputate convallis. Praesent tincidunt tortor et eros volutpat, vitae porta orci euismod. Aliquam faucibus ligula quis suscipit iaculis. Donec id faucibus sem, eget blandit odio. Vivamus ultrices viverra condimentum. Aliquam id tempor odio.\n\nNam sollicitudin dolor ac commodo mattis. Nunc risus turpis, pellentesque eu consequat vel, dignissim non est. Donec gravida pharetra est vel vulputate. Aliquam vestibulum nisi ut commodo pellentesque. Etiam aliquet turpis id urna dapibus suscipit. Aliquam posuere rutrum imperdiet. Curabitur et eros sit amet turpis feugiat blandit sed in libero.\n\nAenean ultricies, dui vel accumsan interdum, odio purus blandit erat, ac facilisis elit ante et odio. Sed gravida sollicitudin tempus. Sed in ornare lacus. Phasellus nec egestas purus. Cras eget ultricies dolor. Duis vel purus et urna elementum aliquet vitae nec libero. Vestibulum vitae porttitor nisi, sit amet fermentum nunc. In mattis semper turpis mollis ultrices. Cras aliquet est velit, sed pellentesque erat fringilla nec. Sed fringilla sem a lacinia congue. Proin vitae aliquet massa. Aliquam massa urna, mattis vitae tellus id, fermentum sodales tellus. Mauris porta lobortis dictum.");
			else
				timer.cancel();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#cancel()
		 */
		@Override
		public boolean cancel() {
			isCancelled = true;

			return super.cancel();
		}
	}

	private static final String LOGIN_CHUCK = "chuck@jouleunit.org";
	private static final String LOGIN_TIFFANY = "tiffany@jouleunit.org";
	private static final String PASSWORD_CHUCK = "naonao";
	private static final String PASSWORD_TIFFANY = "barbie";
	private static final String IMAP_SERVER_URL = "1.mx.vroute.de";
	private static final int PORT = 5725;

	/** {@link Timer} used to run the {@link EmailTask}. */
	private Timer timer;

	/** The currently executed {@link EmailTask}, if any. */
	private EmailTask emailTask;

	public static void main(String args[]) throws IOException {

		EmailService service = new EmailService();

		/* Open socket and wait for clients. */
		while (true) {
			ServerSocket serverSocket = null;

			try {
				serverSocket = new ServerSocket(PORT);
			}

			catch (IOException e) {
				System.err.println("Could not listen on port: " + PORT);
				System.exit(-1);
			}

			Socket clientSocket = null;

			try {
				System.out.println("Wait to accept command ...");
				clientSocket = serverSocket.accept();
			}

			catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(-1);
			}

			ConsoleStream out = new ConsoleStream(
					clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine;

			String help = "Send one of the following commands to:"
					+ "\n1 - delete all messages in Chucks account"
					+ "\n2 - delete all messages in Tiffanys account"
					+ "\n3 - Send a mail from Chuck to Tiffany every minute"
					+ "\n4 - Send a mail from Tiffany to Chuck every five minutes"
					+ "\n5 - Send a mail from Tiffany to Chuck every ten minutes"
					+ "\n6 - Stops sending of mails mail from Tiffany to Chuck";

			out.println("Ready");

			while ((inputLine = in.readLine()) != null) {

				try {
					int command = Integer.parseInt(inputLine.trim());

					switch (command) {

					case 1:
						out.println("Empty Chucks account ...");
						service.emptyAccount(Account.Chuck);
						out.println("... done");
						break;

					case 2:
						out.println("Empty Tiffanys account ...");
						service.emptyAccount(Account.Tiffany);
						out.println("... done");
						break;

					case 3:
						out.println("Tiffany: send Chuck a mail every minute ...");
						service.triggerEmailTask(1);
						out.println("... done");
						break;

					case 4:
						out.println("Tiffany: send Chuck a mail every five minutes ...");
						service.triggerEmailTask(5);
						out.println("... done");
						break;

					case 5:
						out.println("Tiffany: send Chuck a mail every ten minutes ...");
						service.triggerEmailTask(10);
						out.println("... done");
						break;

					case 6:
						out.println("Tiffany:  do not send Chuck mails anymore ...");
						service.cancelEmailTask();
						out.println("... done");
						break;

					default:
						out.println("Unknown Command.\n" + help);
						out.println("abort");
					}

					break;
				}

				catch (NumberFormatException e) {
					out.println("Expected integer but was: " + inputLine + help);
					out.println("abort");
					break;
				}
			}
			// end while.

			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();

			System.out.println("Done\n\n");
		}
		// end while (program termination).
	}

	/**
	 * Helper method canceling the current {@link EmailTask}, if any
	 * {@link EmailTask} is running.
	 */
	protected void cancelEmailTask() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
		// no else.

		if (null != emailTask) {
			emailTask.cancel();
			emailTask = null;
		}
		// no else.
	}

	/**
	 * Helper method deleting all {@link Message}s in all {@link Folder}s of
	 * Chuck's mail account.
	 * 
	 * @param account
	 *            The {@link Account} to be emptied.
	 * @throws MessagingException
	 */
	protected void emptyAccount(Account account) {

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imap");

			switch (account) {
			case Chuck:
				store.connect(IMAP_SERVER_URL, LOGIN_CHUCK, PASSWORD_CHUCK);
				break;
			case Tiffany:
				store.connect(IMAP_SERVER_URL, LOGIN_TIFFANY, PASSWORD_TIFFANY);
				break;
			default:
				return;
			}

			Folder[] folders = store.getDefaultFolder().list();
			for (Folder folder : folders)
				deleteAllMessages(store, folder.getName());
			// end for.

			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	/**
	 * Helper method to send an email.
	 * 
	 * @param from
	 *            The {@link Account} to send the mail from.
	 * @param to
	 *            The {@link Account} to send the mail to.
	 * @param subject
	 *            The subject.
	 * @param text
	 *            The test.
	 */
	protected void sendMail(Account from, Account to, String subject,
			String text) {

		final String username;
		final String password;
		String recipient;

		switch (from) {
		case Chuck:
			username = LOGIN_CHUCK;
			password = PASSWORD_CHUCK;
			recipient = LOGIN_TIFFANY;
			break;
		case Tiffany:
			username = LOGIN_TIFFANY;
			password = PASSWORD_TIFFANY;
			recipient = LOGIN_CHUCK;
			break;
		default:
			return;
		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", IMAP_SERVER_URL);
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					recipient));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	/**
	 * Helper method triggering a new {@link EmailTask} every given minutes.
	 * 
	 * @param minutes
	 *            The amount of time in minutes between the individual
	 *            executions of the {@link EmailTask}.
	 */
	protected void triggerEmailTask(int minutes) {
		if (null != timer)
			cancelEmailTask();
		// no else.

		timer = new Timer();
		emailTask = new EmailTask();
		timer.schedule(emailTask, minutes * 60000, minutes * 60000);
	}

	/**
	 * Helper method deleting all {@link Message}s within a {@link Folder} of a
	 * given {@link Store} identified by its name.
	 * 
	 * @param store
	 * @param folder
	 * @throws MessagingException
	 */
	private void deleteAllMessages(Store store, String folder)
			throws MessagingException {

		Folder inbox = store.getFolder(folder);
		inbox.open(Folder.READ_WRITE);
		Message messages[] = inbox.getMessages();

		if (messages.length > 0) {
			Flags deleted = new Flags(Flags.Flag.DELETED);
			inbox.setFlags(messages, deleted, true);
			inbox.expunge();
			System.out.println("Deleted " + messages.length + " messages in "
					+ folder + ".");
		}
		// no else.
	}
}
