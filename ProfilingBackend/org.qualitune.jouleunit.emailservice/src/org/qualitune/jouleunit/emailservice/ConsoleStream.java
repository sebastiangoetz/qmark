package org.qualitune.jouleunit.emailservice;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * {@link PrintStream} extension to cache the printed information.
 * 
 * @author Claas Wilke
 */
public class ConsoleStream extends PrintStream {

	/** {@link StringBuilder} used to cache all printed messages. */
	private StringBuilder mBuilder;

	/**
	 * Creates a new {@link ConsoleStream}.
	 * 
	 * @param out
	 *            The {@link OutputStream} to which all messages are forwarded.
	 */
	public ConsoleStream(OutputStream out) {
		super(out);
		mBuilder = new StringBuilder();
	}

	/**
	 * Returns all yet printed messages as a single {@link String}.
	 * 
	 * @return All yet printed messages as a single {@link String}.
	 */
	public String getPrintedInformation() {
		return mBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.PrintStream#println(java.lang.String)
	 */
	@Override
	public void println(String x) {
		super.println(x);
		mBuilder.append(x + "\n");
		System.out.println(x);
	}
}
