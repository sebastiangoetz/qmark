package org.qualitune.jouleunit.androidremote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.adakoda.android.asm.MainFrame;
import com.android.ddmlib.IDevice;

/**
 * Main window of {@link AndroidRemote}.
 * 
 * @author Claas Wilke
 */
public class MainWindow extends JFrame {

	/** Background {@link Color}. */
	private static final Color BACKGROUND_COLOR = Color.BLACK;

	/** Foreground {@link Color}. */
	private static final Color FOREGROUND_COLOR = Color.WHITE;

	/** Generated serialization ID. */
	private static final long serialVersionUID = 3353324406248296199L;

	/** {@link JLabel} to display the coordinates. */
	private JLabel coordinateLabel;

	/** The {@link IDevice} of the main frame. */
	private IDevice device;

	/** The {@link MainFrame} of this {@link MainWindow}. */
	private MainFrame mainFrame;

	/** JLabel to display status information. */
	private JLabel statusLabel;

	/**
	 * Creates a new {@link MainWindow}.
	 * 
	 * @param mainFrame
	 *            The {@link MainFrame} of this {@link MainWindow}.
	 */
	public MainWindow(MainFrame mainFrame) {

		/* Try to grab the android device to be used. */
		try {
			Field field;
			field = MainFrame.class.getDeclaredField("mDevice");
			field.setAccessible(true);
			device = (IDevice) field.get(mainFrame);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		this.mainFrame = mainFrame;

		initializeUI(mainFrame);
	}

	/**
	 * Updates the Status label of this {@link MainWindow} with a given status
	 * message.
	 * 
	 * @param msg
	 *            The message used for the update.
	 */
	public void updateStatusLabel(String msg) {
		this.statusLabel.setText(msg);
	}

	/**
	 * {@link MouseListener} implementation to trigger click and swipe events on
	 * the remote device.
	 */
	private class MainFramewMouseListener implements MouseListener {

		/* Array used to store the start position of a swipe event. */
		private int[] pos;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			/* Nothing. */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			pos = new int[] { e.getX(), e.getY() };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			if (pos.length > 1) {
				String command = "input ";
				if (e.getX() == pos[0] && e.getY() == pos[1])
					command += "tap " + e.getX() + " " + e.getY();
				else
					command += "swipe " + pos[0] + " " + pos[1] + " "
							+ e.getX() + " " + e.getY();

				try {
					device.executeShellCommand(command, new AdbOutputReceiver(
							MainWindow.this));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				mainFrame.startMonitor();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			/* Nothing. */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
			/* Nothing. */
		}
	}

	/**
	 * {@link MouseMotionListener} implementation to update the coordinates of
	 * the cursor.
	 */
	private class MainFrameMouseMotionListener implements MouseMotionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
		 * )
		 */
		public void mouseDragged(MouseEvent e) {
			/* Empty. */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent
		 * )
		 */
		public void mouseMoved(MouseEvent e) {
			coordinateLabel.setText("(" + e.getX() + ", " + e.getY() + ")");
		}
	}

	private void initializeUI(MainFrame mainFrame) {

		/* Main panel (background). */
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		this.getContentPane().add(mainPanel);

		initializeControl(mainPanel);
		initializeScreenView(mainPanel);

		/* Window size. */
		// this.setSize(coordinatePanel.getWidth()
		// + mainFrame.getContentPane().getWidth(), mainFrame
		// .getContentPane().getHeight() + 40);
		this.setSize(900, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initializeScreenView(JPanel mainPanel) {

		mainFrame.stopMonitor();

		/* The main frame. */
		JPanel demoPanel = new JPanel();
		demoPanel.setBackground(BACKGROUND_COLOR);
		demoPanel.setLayout(new BorderLayout());
		demoPanel.setPreferredSize(new Dimension(720, 1280));
		demoPanel.add(mainFrame.getContentPane(), BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(demoPanel);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(mainFrame.getContentPane().getSize());

		mainPanel.add(scrollPane, BorderLayout.CENTER);

		/* Mouse listeners. */
		mainFrame.getContentPane().addMouseMotionListener(
				new MainFrameMouseMotionListener());
		mainFrame.getContentPane().addMouseListener(
				new MainFramewMouseListener());
	}

	private void initializeControl(JPanel mainPanel) {
		JPanel coordinatePanel = new JPanel();
		// coordinatePanel.setLayout(new GridLayout(4, 1));
		coordinatePanel.setPreferredSize(new Dimension(144, 500));
		coordinatePanel.setBackground(BACKGROUND_COLOR);

		JButton powerButton = new JButton("Display On/Off");
		powerButton.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				try {
					device.executeShellCommand("input keyevent 26",
							new AdbOutputReceiver(MainWindow.this));
				} catch (IOException e1) {
					statusLabel.setText("Exception: " + e1.getMessage());
				}
			}
		});
		coordinatePanel.add(powerButton);

		JLabel titleLabel = new JLabel("Coordinates:");
		titleLabel.setForeground(FOREGROUND_COLOR);
		coordinatePanel.add(titleLabel);

		coordinateLabel = new JLabel("(0, 0)");
		coordinateLabel.setForeground(FOREGROUND_COLOR);
		coordinatePanel.add(coordinateLabel);

		statusLabel = new JLabel();
		statusLabel.setForeground(FOREGROUND_COLOR);
		coordinatePanel.add(statusLabel);

		mainPanel.add(coordinatePanel, BorderLayout.WEST);
	}
}