package org.qualitune.jouleunit.androidremote;

import com.adakoda.android.asm.AndroidScreenMonitor;
import com.adakoda.android.asm.MainFrame;

/**
 * Main class of {@link AndroidRemote}.
 * 
 * @author Claas Wilke
 */
public class AndroidRemote {

	/**
	 * {@link MainFrame} from {@link AndroidScreenMonitor} used to show device
	 * screen.
	 */
	private MainWindow mainWindow;

	/** Creates a new {@link AndroidRemote}. */
	public AndroidRemote() {
	}

	/** Initializes this {@link AndroidRemote}. */
	public void initialize() {
		MainFrame mainFrame = new MainFrame(new String[0]);
		mainFrame.selectDevice();
		
		try {
		Thread.sleep(2000);
		}
		
		catch (InterruptedException e) {
			/* Not important. */
		}

		mainWindow = new MainWindow(mainFrame);
		mainWindow.setVisible(true);

		// mainFrame.setLocationRelativeTo(null);
		// // mainFrame.setVisible(true);
	}

	/**
	 * Main method to start {@link AndroidRemote}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		AndroidRemote remote = new AndroidRemote();
		remote.initialize();
	}
}
