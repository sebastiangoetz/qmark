package org.tud.qmark.sessionmgmt;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.TestSuite;
import org.tud.qmark.entities.Version;

/**
 * Responsible to upload an APK file (either for a {@link Version} or an
 * {@link TestSuite}.
 * 
 * @author Claas Wilke
 */
@RequestScoped
@ManagedBean
public class FileUpload implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -2146337506648861148L;

	/**
	 * The {@link Account} used to manage associated {@link UploadedFile}s with
	 * {@link Version}s etc.
	 */
	@Inject
	private Account account;

	/**
	 * Indicates what to do with the uploaded file (e.g., whether to add an APK
	 * file to a {@link Version} or a {@link TestSuite}.
	 */
	private String fileTarget;

	/** The {@link UploadedFile} representing a {@link Binary}. */
	private UploadedFile uploadedFile;

	/**
	 * Returns the indicator what to do with the uploaded file (e.g., whether to
	 * add an APK file to a {@link Version} or a {@link TestSuite}.
	 * 
	 * @return The indicator what to do with the uploaded file (e.g., whether to
	 *         add an APK file to a {@link Version} or a {@link TestSuite}.
	 */
	public String getFileTarget() {
		return fileTarget;
	}

	/**
	 * Returns the {@link UploadedFile} representing a {@link Binary}.
	 * 
	 * @return The {@link UploadedFile} representing a {@link Binary}.
	 */
	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	/**
	 * Sets the indicator what to do with the uploaded file (e.g., whether to
	 * add an APK file to a {@link Version} or a {@link TestSuite}.
	 * 
	 * @param fileTarget
	 *            The indicator what to do with the uploaded file (e.g., whether
	 *            to add an APK file to a {@link Version} or a {@link TestSuite}
	 *            .
	 */
	public void setFileTarget(String fileTarget) {
		this.fileTarget = fileTarget;
	}

	/**
	 * Sets the {@link UploadedFile} representing a {@link Binary}.
	 * 
	 * @param uploadedFile
	 *            The {@link UploadedFile} representing a {@link Binary}.
	 */
	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	/**
	 * Uploads a file and tries to store it in the DB.
	 * 
	 * @throws IOException
	 */
	public boolean uploadFile() throws IOException {

		if (account.canEditVersion(account.getSelectedVersion())) {

			if (uploadedFile.getContentType().equals("application/zip") || 
					uploadedFile.getContentType().equals("application/octet-stream")) {
				Binary binary = new Binary();
				binary.setContent(uploadedFile.getBytes());
				binary.setName(uploadedFile.getName());
				binary.setContentType(uploadedFile.getContentType());

				if (null != fileTarget && fileTarget.equals("version")) {
					account.getSelectedVersion().setApk(binary);

					if (account.updateVersion(account.getSelectedVersion())) {

						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(String.format(
										"File '%s' successfully uploaded!",
										binary.getName())));
						return true;
					}

					else
						return false;
				}

				else if (null != fileTarget && fileTarget.equals("testsuite")) {
					account.getSelectedTestSuite().setApk(binary);

					if (account.updateTestSuite(account.getSelectedTestSuite())) {

						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(String.format(
										"File '%s' successfully uploaded!",
										binary.getName())));
						return true;
					}

					else
						return false;
				}

				else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(String
									.format("Unknown file target '%s'",
											getFileTarget())));
					return false;
				}
			}

			else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Wrong content type ("+uploadedFile.getContentType()+"). Application expected."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have permissions to upload a file for this version."));
			return false;
		}
	}
}