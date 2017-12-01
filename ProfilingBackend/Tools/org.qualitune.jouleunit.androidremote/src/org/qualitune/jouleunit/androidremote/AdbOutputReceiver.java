package org.qualitune.jouleunit.androidremote;

import com.android.ddmlib.IShellOutputReceiver;

/**
 * {@link IShellOutputReceiver} to receive the response of an executed ABD
 * command.
 * 
 * @author Claas Wilke
 */
public class AdbOutputReceiver implements IShellOutputReceiver {

	/**
	 * The {@link MainWindow} this {@link AdbOutputReceiver} belongs to.
	 */
	private MainWindow mainWindow;

	/**
	 * Creates a new {@link AdbOutputReceiver}.
	 * 
	 * @param mainWindow
	 *            The {@link MainWindow} this {@link AdbOutputReceiver} belongs
	 *            to.
	 */
	public AdbOutputReceiver(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.ddmlib.IShellOutputReceiver#addOutput(byte[], int, int)
	 */
	public void addOutput(byte[] arg0, int arg1, int arg2) {
		mainWindow.updateStatusLabel(new String(arg0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.ddmlib.IShellOutputReceiver#flush()
	 */
	public void flush() {
		/* Nothing. */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.ddmlib.IShellOutputReceiver#isCancelled()
	 */
	public boolean isCancelled() {
		return false;
	}
}