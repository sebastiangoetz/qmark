/*
 * Created on 24.10.2012
 */

package org.tud.qmark.entities;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author Sebastian Richy - maquiz@googlemail.com
 * 
 */

@RequestScoped
@Named
public class Credentials implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -678922277563922672L;

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}