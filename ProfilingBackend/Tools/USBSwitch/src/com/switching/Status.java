package com.switching;

public class Status {

	private USBPort usbPort = USBPort.UNKNOWN;
	private Power power = Power.UNKNOWN;
	private double temperature = Double.NaN;
	private Power autoPowerOn = Power.UNKNOWN;
	
	/**
	 * Get the status of the auto power on setting.
	 * @return
	 */
	public Power getAutoPowerOn() {
		return autoPowerOn;
	}

	/**
	 * Set the status of the auto power on setting.
	 * @return
	 */
	public void setAutoPowerOn(Power autoPowerOn) {
		this.autoPowerOn = autoPowerOn;
	}

	/**
	 * Get the current online USB Port status.
	 * @return
	 */
	public USBPort getUsbPort() {
		return usbPort;
	}
	
	/**
	 * Set the current online USB Port status.
	 * @return
	 */
	public void setUsbPort(USBPort usbPort) {
		this.usbPort = usbPort;
	}
	
	/**
	 * Get the temperature.
	 * @return
	 */
	public double getTemperature() {
		return temperature;
	}
	
	/**
	 * Set the temperature.
	 * @param temp
	 */
	public void setTemperature(double temp)
	{
		temperature = temp;
	}

	/**
	 * Get the status of the current power setting.
	 * @return
	 */
	public Power getPower() {
		return power;
	}
	
	/**
	 * Set the status of the current power setting.
	 * @return
	 */
	public void setPower(Power power) {
		this.power = power;
	}

	/**
	 * Constructor
	 */
	public Status()
	{
	}
}
