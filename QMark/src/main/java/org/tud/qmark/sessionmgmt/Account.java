package org.tud.qmark.sessionmgmt;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omg.CORBA.UserException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.tud.qmark.entities.App;
import org.tud.qmark.entities.Binary;
import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Credentials;
import org.tud.qmark.entities.Device;
import org.tud.qmark.entities.EnergyModel;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.PaymentPlan;
import org.tud.qmark.entities.PowerRate;
import org.tud.qmark.entities.TestCase;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.TestSuite;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;
import org.tud.qmark.entities.UserMetaData;
import org.tud.qmark.entities.Vendor;
import org.tud.qmark.entities.Version;
import org.tud.qmark.interfaces.IAppManager;
import org.tud.qmark.interfaces.IDeviceManager;
import org.tud.qmark.interfaces.IMemberManager;
import org.tud.qmark.interfaces.IPaymentPlanManager;
import org.tud.qmark.interfaces.ITestSuiteManager;
import org.tud.qmark.interfaces.ITypeManager;
import org.tud.qmark.interfaces.IUserManager;
import org.tud.qmark.interfaces.IVendorManager;
import org.tud.qmark.interfaces.IVersionManager;
import org.tud.qmark.util.BenchmarkUtil;
import org.tud.qmark.util.LoggedIn;
import org.tud.qmark.util.TypeConstants;

/**
 * Manages the current session state for account-specific states.
 * 
 * @author Claas Wilke
 */
@SessionScoped
@Named
public class Account implements Serializable {

	/** Generated ID for serialization. */
	private static final long serialVersionUID = -4146846219865244526L;

	/** The {@link IAppManager} used to manage {@link App} entities. */
	@Inject
	private IAppManager appManager;

	/** The {@link Credentials} used for login. */
	@Inject
	private Credentials credentials;

	/** The current {@link User} if logged in, else null. */
	private User currentUser;

	/** The {@link IDeviceManager} used to manage {@link Device} entities. */
	@Inject
	private IDeviceManager deviceManager;

	/**
	 * Indicates whether the currently edited {@link App} is a new {@link App}
	 * not yet persisted to the DB.
	 */
	private boolean isNewApp;

	/**
	 * Indicates whether the currently edited {@link CompanyMember} is a new
	 * {@link CompanyMember} not yet persisted to the DB.
	 */
	private boolean isNewCompanyMember;

	/**
	 * Indicates whether the current {@link User} is already registered or is
	 * new.
	 */
	private boolean isNewUser;

	/**
	 * Indicates whether the currently edited {@link TestRun} is a new
	 * {@link TestRun} not yet persisted to the DB.
	 */
	private boolean isNewTestRun;

	/**
	 * Indicates whether the currently edited {@link TestSuite} is a new
	 * {@link TestSuite} not yet persisted to the DB.
	 */
	private boolean isNewTestSuite;

	/**
	 * Indicates whether the currently edited {@link Vendor} is a new
	 * {@link Vendor} not yet persisted to the DB.
	 */
	private boolean isNewVendor;

	/**
	 * Indicates whether the currently edited {@link Version} is a new
	 * {@link Version} not yet persisted to the DB.
	 */
	private boolean isNewVersion;

	/** The {@link IMemberManager} used to manage {@link Member} entities. */
	@Inject
	private IMemberManager memberManager;

	/**
	 * The email address of a {@link User} that shall be invited as a
	 * {@link CompanyMember} for the currently selected {@link Vendor} or as a
	 * {@link Member} for the currently selected {@link App}.
	 */
	private String newMemberMail;

	/**
	 * The {@link IPaymentPlanManager} used to manage {@link PaymentPlan}
	 * entities.
	 */
	@Inject
	private IPaymentPlanManager paymentPlanManager;

	/**
	 * The currently selected {@link App} (project) to be displayed in the
	 * account.
	 */
	private App selectedApp;

	/**
	 * The currently selected {@link CompanyMember} to be displayed in the
	 * account.
	 */
	private CompanyMember selectedCompanyMember;

	/**
	 * The currently selected {@link Member} belonging to the currently selected
	 * {@link App}.
	 */
	private Member selecteMember;

	/**
	 * The currently selected {@link TestCase} to be displayed in the account.
	 */
	private TestCase selectedTestCase;

	/**
	 * The currently selected {@link TestRun} to be displayed in the account.
	 */
	private TestRun selectedTestRun;

	/**
	 * The currently selected {@link TestSuite} to be displayed in the account.
	 */
	private TestSuite selectedTestSuite;

	/**
	 * The currently selected {@link Vendor} to be displayed in the account.
	 */
	private Vendor selectedVendor;

	/**
	 * The currently selected {@link Version} to be displayed in the account.
	 */
	private Version selectedVersion;

	/** The {@link ITestSuiteManager} used to manage {@link TestSuite} entities. */
	@Inject
	private ITestSuiteManager testSuiteManager;

	/** The {@link ITypeManager} used to manage {@link Type} entities. */
	@Inject
	private ITypeManager typeManager;

	/** The {@link IUserManager} used to manage {@link User} entities. */
	@Inject
	private IUserManager userManager;

	/** The {@link IVendorManager} used to manage {@link Vendor} entities. */
	@Inject
	private IVendorManager vendorManager;

	/** The {@link IVersionManager} used to manage {@link Version} entities. */
	@Inject
	private IVersionManager versionManager;

	/**
	 * Let the currently logged in {@link User} accept a {@link Member}
	 * invitation.
	 * 
	 * @param member
	 *            The {@link Member} to be accepted.
	 * @return <code>true</code> if accepted successfully.
	 */
	public boolean acceptAppMemberInvitation(Member member) {

		if (canAcceptAppMemberInvitation(member)) {
			try {
				member.setType(typeManager
						.getType(TypeConstants.APP_MEMBER_TYPE_MEMBER));
				memberManager.updateMember(member);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Membership successfully accepted."));

				return true;
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during accept of app membership."));
				return false;
			}
		}

		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Cannot accept this invitation."));
			return false;
		}
	}

	/**
	 * Let the currently logged in {@link User} accept a {@link CompanyMember}
	 * invitation.
	 * 
	 * @param member
	 *            The {@link CompanyMember} to be accepted.
	 * @return <code>true</code> if accepted successfully.
	 */
	public boolean acceptCompanyMemberInvitation(CompanyMember member) {

		if (canAcceptCompanyMemberInvitation(member)) {
			try {
				member.setType(typeManager
						.getType(TypeConstants.COMPANY_MEMBER_TYPE_MEMBER));
				vendorManager.updateCompanyMember(member);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Membership successfully accepted."));

				return true;
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during accept of company membership."));
				return false;
			}
		}

		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Cannot accept this invitation."));
			return false;
		}
	}
	
	/**
	 * Let the currently logged in {@link User} accept a user registration.
	 * 
	 * @param u
	 *            The {@link User} to be accepted.
	 * @return <code>true</code> if accepted successfully.
	 */
	public boolean acceptUserRegistration(User u) {

		if (canAcceptUserRegistration()) {
			try {
				u.setStatus(typeManager
						.getType("active"));
				userManager.updateUser(u);
				
				List<User> pending = userManager.getUserRegistrations();
				if(pending.contains(u)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("User ("+u.getLogin()+") still pending."));
					
				} else {

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("User ("+u.getLogin()+") successfully accepted."));
				}

				return true;
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during accept of user registration."));
				return false;
			}
		}

		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("The current user is not allowed to accept this registration."));
			return false;
		}
	}

	/**
	 * Checks whether the currently logged in user can accept a {@link Member}
	 * invitation.
	 * 
	 * @return If <code>true</code> he can accept the invitation.
	 */
	public boolean canAcceptAppMemberInvitation(Member member) {

		if (null != member
				&& member.getType().isOfType(
						TypeConstants.APP_MEMBER_TYPE_INVITED)
				&& member.getUser().getUserID()
						.equals(getCurrentUser().getUserID()))
			return true;

		else
			return false;
	}

	/**
	 * Checks whether the currently logged in user can accept a
	 * {@link CompanyMember} invitation.
	 * 
	 * @return If <code>true</code> he can accept the invitation.
	 */
	public boolean canAcceptCompanyMemberInvitation(CompanyMember member) {

		if (null != member
				&& member.getType().isOfType(
						TypeConstants.COMPANY_MEMBER_TYPE_INVITED)
				&& member.getUserMetaData().equals(
						getCurrentUser().getUserMetaData()))
			return true;

		else
			return false;
	}

	/**
	 * Checks whether the currently logged in {@link User} can add a given
	 * {@link User} to a given {@link App}.
	 * 
	 * @param user
	 *            The {@link User} to be added.
	 * @param app
	 *            The {@link App}.
	 * @return <code>true</code> if the {@link User} can be added.
	 */
	public boolean canAddUserToApp(User user, App app) {

		if (canEditApp(app, null)) {
			/* Check that user does not yet belong to the app. */
			try {
				for (Member member : user.getMemberships()) {
					if (member.getApp().equals(app)) {
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												"This user already belongs to this app."));
						return false;
					}
					// no else.
				}
				// end for.
			} catch (Exception e) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during check for permission to add a user to an app."));
				return false;
			}

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to add users to this app."));
			return false;
		}
	}

	/**
	 * Checks whether the currently logged in {@link User} can add a given
	 * {@link User} to a given {@link Vendor}.
	 * 
	 * @param user
	 *            The {@link User} to be added.
	 * @param vendor
	 *            The {@link Vendor}.
	 * @return <code>true</code> if the {@link User} can be added.
	 */
	public boolean canAddUserToVendor(User user, Vendor vendor) {

		if (canEditVendor(vendor)) {
			/* Check that user does not yet belong to the company. */
			try {
				for (CompanyMember member : userManager.getCompanyMembers(user)) {
					if (member.getVendor().getVendorID()
							.equals(selectedVendor.getVendorID())) {
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												"This user already belongs to this company."));
						return false;
					}
					// no else.
				}
				// end for.
			} catch (Exception e) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during check for permission to add a user to a company."));
				return false;
			}

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to add users to this company."));
			return false;
		}
	}

	/**
	 * Checks whether the currently logged in {@link User} can create a new
	 * {@link App}.
	 * 
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         create a new {@link App}.
	 */
	public boolean canCreateNewApp() {

		/* Currently all users are allowed to create new apps. */
		return true;
	}

	/**
	 * Checks whether the currently logged in {@link User} can create a new
	 * {@link TestSuite} for the currently selected {@link TestSuite}.
	 * 
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         create a new {@link TestSuite} for the currently selected
	 *         {@link Version}.
	 */
	public boolean canCreateNewTestSuite() {

		/*
		 * If the user is allowed to edit the version, he is allowed to create
		 * new test suites.
		 */
		return canEditVersion(getSelectedVersion());
	}
	
	/**
	 * Verifies, whether a {@link User} (currently loggedin) is allowed to
	 * accept user registrations.
	 * 
	 * @return <code>true</code> if the logged in {@link User} is allowed to
	 *         do so.
	 */
	public boolean canAcceptUserRegistration() {

		if (currentUser.getStatus().isOfType("admin"))
			return true;

		else
			return false;
	}

	/**
	 * Verifies, whether a {@link User} (currently loggedin) is allowed to
	 * create a new {@link Vendor}.
	 * 
	 * @return <code>true</code> if the logged in {@link User} is allowed to
	 *         create a new {@link Vendor}.
	 */
	public boolean canCreateNewVendor() {

		/* Demo users are not allowed create new vendors. */
		if (currentUser.getStatus().isOfType(TypeConstants.USER_TYPE_DEMO))
			return false;

		else
			return true;
	}

	/**
	 * Checks whether the currently logged in {@link User} can create a new
	 * {@link Version} for the currently selected {@link App}.
	 * 
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         create a new {@link Version} for the currently selected
	 *         {@link App}.
	 */
	public boolean canCreateNewVersion() {

		/*
		 * If the user is allowed to edit the app, he is allowed to create new
		 * versions.
		 */
		return canEditApp(getSelectedApp(), null);
	}

	/**
	 * Checks whether a given {@link Member} or the currently logged in
	 * {@link User} can edit a given {@link App}.
	 * 
	 * @param app
	 *            The {@link App} to be edited
	 * @param The
	 *            {@link Member} to open the {@link App}. Can be
	 *            <code>null</code>, if all {@link Member}s of the currently
	 *            logged in {@link User} should be tested instead.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         edit the given {@link App}.
	 */
	public boolean canEditApp(App app, Member member) {

		if (isLoggedIn()) {
			try {
				/* Only owners can edit an app. */
				if (null != member)
					return (member.getApp().getAppID().equals(app.getAppID()) && member
							.getType().isOfType(
									TypeConstants.APP_MEMBER_TYPE_APP_OWNER));
				if (null != getSelecteMember()
						&& getSelecteMember().getApp().getAppID()
								.equals(app.getAppID())
						&& getSelecteMember().getType().isOfType(
								TypeConstants.APP_MEMBER_TYPE_APP_OWNER))
					return true;
				else
					return memberManager
							.isMemberOfType(
									app,
									getCurrentUser(),
									typeManager
											.getType(TypeConstants.APP_MEMBER_TYPE_APP_OWNER));
			} catch (Exception e) {
				return false;
			}
			// end for.
		}

		else
			return false;
	}

	/**
	 * Verifies, whether a {@link User} (currently logged in) is allowed to edit
	 * the settings of a given {@link User}.
	 * 
	 * @param user
	 *            The {@link User} to be edit.
	 * @return <code>true</code> if the logged in {@link User} is allowed to
	 *         edit the settings of the given {@link User}.
	 */
	public boolean canEditSettings(User user) {

		if (isLoggedIn()
				&& getCurrentUser().getUserID().equals(user.getUserID()))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether the currently logged in {@link User} can edit a given
	 * {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be edited
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         edit the given {@link TestSuite}.
	 */
	public boolean canEditTestSuite(TestSuite testSuite) {

		/*
		 * If the user can edit the test suite's version, he is allowed to edit
		 * the test suite.
		 */
		if (null != testSuite)
			return canEditVersion(testSuite.getVersion());
		else
			return false;
	}

	/**
	 * Verifies, whether a {@link User} (currently logged in) is allowed to edit
	 * the settings of a given {@link Vendor}.
	 * 
	 * @param vendor
	 *            The {@link Vendor} to be edit.
	 * @return <code>true</code> if the logged in {@link User} is allowed to
	 *         edit the settings of the given {@link Vendor}.
	 */
	public boolean canEditVendor(Vendor vendor) {

		if (isLoggedIn()) {
			try {
				for (CompanyMember member : vendorManager
						.getCompanyMembers(vendor)) {
					if (((Long)(member
							.getUserMetaData()
							.getUserMetaDataID()))
							.equals(getCurrentUser().getUserMetaData()
									.getUserMetaDataID())
							&& member
									.getType()
									.isOfType(
											TypeConstants.COMPANY_MEMBER_TYPE_COMPANY_OWNER))
						return true;
					// no else.
				}
				// end for.
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during retrieval of company members."));
			}

			return false;
		} else
			return false;
	}

	/**
	 * Checks whether the currently logged in {@link User} can edit a given
	 * {@link Version}.
	 * 
	 * @param version
	 *            The {@link Version} to be edited
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         edit the given {@link Version}.
	 */
	public boolean canEditVersion(Version version) {

		/*
		 * If the user can edit the app of the version, he is allowed to edit
		 * the version.
		 */
		return canEditApp(version.getApp(), null);
	}

	/**
	 * Checks whether the currently logged in {@link User} can delete a given
	 * TestRun.
	 * 
	 * @param testRun
	 *            The {@link TestRun} that shall be deleted.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         delete the given {@link TestRun}.
	 */
	public boolean canDeleteTestRun(TestRun testRun) {

		/* If the user can edit the test suite, he can delete a new test run. */
		return canEditTestSuite(testRun.getTestSuite());
	}

	/**
	 * Checks whether the currently logged in {@link User} can trigger a new
	 * TestRun for a given {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} for which a new {@link TestRun} shall be
	 *            triggered.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         trigger a new {@link TestRun} for the given {@link TestSuite}.
	 */
	public boolean canTriggerTestRun(TestSuite testSuite) {

		/* If the user can edit the test suite, he can trigger a new test run. */
		return canEditTestSuite(testSuite);
	}

	/**
	 * Checks whether a given {@link Member} (or the currently logged in
	 * {@link User}) can view the settings of a given {@link App}.
	 * 
	 * @param app
	 *            The {@link App} to be opened.
	 * @param The
	 *            {@link Member} to open the {@link App}. Can be
	 *            <code>null</code>, if all {@link Member}s of the currently
	 *            logged in {@link User} should be tested instead.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         view the given {@link App}.
	 */
	public boolean canViewApp(App app, Member member) {

		if (isLoggedIn()) {
			if (null != member)
				return member.getApp().getAppID().equals(app.getAppID());
			else if (null != getSelecteMember()
					&& getSelecteMember().getApp().getAppID()
							.equals(app.getAppID()))
				return true;
			else {
				try {
					/* All members can view an app. */
					return memberManager.isMember(app, getCurrentUser());
				} catch (Exception e) {
					return false;
				}
				// end for.
			}
		}

		else
			return false;
	}

	/**
	 * Checks whether the currently logged in {@link User} can view the
	 * properties of a given {@link TestCase}.
	 * 
	 * @param testCase
	 *            The {@link TestCase} to be opened.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         view the given {@link TestCase}.
	 */
	public boolean canViewTestCase(TestCase testCase) {

		/* If the user can view the test run, he can view the test suite. */
		try {
			return canViewTestRun(testSuiteManager.getTestRun(testCase
					.getTestRunID()));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks whether the currently logged in {@link User} can view the
	 * properties of a given {@link TestRun}.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be opened.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         view the given {@link TestRun}.
	 */
	public boolean canViewTestRun(TestRun testRun) {

		/* If the user can view the test suite, he can view the test run. */
		return canViewTestSuite(testRun.getTestSuite());
	}

	/**
	 * Checks whether the currently logged in {@link User} can view the settings
	 * of a given {@link TestSuite}.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be opened.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         view the given {@link TestSuite}.
	 */
	public boolean canViewTestSuite(TestSuite testSuite) {

		/* If the user can view the version, he can view the test suite. */
		return canViewVersion(testSuite.getVersion());
	}

	/**
	 * Verifies, whether a {@link User} (currently logged in) is allowed to view
	 * the settings of a given {@link Vendor}.
	 * 
	 * @param vendor
	 *            The {@link Vendor} to be edit.
	 * @return <code>true</code> if the logged in {@link User} is allowed to
	 *         view the settings of the given {@link Vendor}.
	 */
	public boolean canViewVendor(Vendor vendor) {

		if (isLoggedIn()) {
			try {
				for (CompanyMember member : vendorManager
						.getCompanyMembers(vendor)) {
					if (((Long)(member
							.getUserMetaData()
							.getUserMetaDataID()))
							.equals(getCurrentUser().getUserMetaData()
									.getUserMetaDataID())
							&& member.getType().isOfType(
									TypeConstants.COMPANY_MEMBER_TYPE))
						return true;
					// no else.
				}
				// end for.
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during retrieval of company members."));
			}

			return false;
		} else
			return false;
	}

	/**
	 * Checks whether the currently logged in {@link User} can view the settings
	 * of a given {@link Version}.
	 * 
	 * @param version
	 *            The {@link Version} to be opened.
	 * @return If <code>true</code>, the currently selected {@link User} can
	 *         view the given {@link Version}.
	 */
	public boolean canViewVersion(Version version) {

		/* If the user can view the version's app, he can view the version. */
		return canViewApp(version.getApp(), null);
	}

	/**
	 * Tries to add the currently selected {@link App} as a new {@link App} to
	 * the DB.
	 */
	public boolean createApp() {

		if (isNewApp && canCreateNewApp()) {
			try {
				appManager.addApp(selectedApp);
				setNewApp(false);

				Member member = new Member();
				member.setApp(selectedApp);
				member.setType(typeManager
						.getType(TypeConstants.APP_MEMBER_TYPE_APP_OWNER));
				member.setUser(getCurrentUser());
				memberManager.addMember(member);
				setSelecteMember(member);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("App successfully created."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Error during creation of app."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new app."));
			return false;
		}
	}

	/**
	 * Tries to find a {@link User} for the current email address for a new
	 * {@link CompanyMember} and adds this {@link User} as a
	 * {@link CompanyMember} to the currently selected {@link Vendor}.
	 */
	public boolean createCompanyMember() {

		if (null != newMemberMail && newMemberMail.length() > 0) {
			try {
				/* Find user for mail. */
				User user = userManager.getUser(newMemberMail);

				if (null == user) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									"No user for this email address exists."));
					return false;
				}
				// no else.

				else if (canAddUserToVendor(user, getSelectedVendor())) {

					/* invite the user. */
					CompanyMember member = new CompanyMember();
					member.setType(typeManager
							.getType(TypeConstants.COMPANY_MEMBER_TYPE_INVITED));
					member.setVendor(selectedVendor);
					member.setUserMetaData(user.getUserMetaData());
					vendorManager.addCompanyMember(member);

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("User successfully invited."));

					return true;
				}

				else
					/* Error message in check method generated. */
					return false;
			}

			catch (Exception e) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during add of user to company."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"No valid email address for a user to be added set."));
			return false;
		}
	}

	/**
	 * Tries to find a {@link User} for the current email address for a new
	 * {@link Member} and adds this {@link User} as a {@link Member} to the
	 * currently selected {@link App}.
	 */
	public boolean createMember() {

		if (null != newMemberMail && newMemberMail.length() > 0) {
			try {
				/* Find user for mail. */
				User user = userManager.getUser(newMemberMail);

				if (null == user) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									"No user for this email address exists."));
					return false;
				}
				// no else.

				else if (canAddUserToApp(user, getSelectedApp())) {

					/* invite the user. */
					Member member = new Member();
					member.setType(typeManager
							.getType(TypeConstants.APP_MEMBER_TYPE_INVITED));
					member.setApp(getSelectedApp());
					member.setUser(user);
					memberManager.addMember(member);

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("User successfully invited."));

					return true;
				}

				else
					/* Error message in check method generated. */
					return false;
			}

			catch (Exception e) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during add of user to company."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"No valid email address for a user to be added set."));
			return false;
		}
	}

	/**
	 * Tries to add the currently selected {@link TestSuite} as a new
	 * {@link TestSuite} to the DB.
	 */
	public boolean createTestSuite() {
		if (isNewTestSuite() && canCreateNewTestSuite()) {
			try {
				testSuiteManager.addTestSuite(getSelectedTestSuite());
				setNewTestSuite(false);

				Set<TestSuite> testSuites = getSelectedVersion()
						.getTestSuites();
				testSuites.add(getSelectedTestSuite());
				getSelectedVersion().setTestSuites(testSuites);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Test suite successfully created."));
				return true;
			}

			catch (Exception e) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during creation of test suite."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new test suite for this version."));
			return false;
		}
	}

	/**
	 * Tries to add the current {@link User} as a new {@link User} to the DB.
	 */
	public boolean createUser() {
		if (isNewUser()) {
			try {
				Type registeredType = typeManager
						.getType(TypeConstants.USER_TYPE_REGISTERED);
				Type activeType = typeManager
						.getType("active");
				currentUser.setStatus(activeType);
				userManager.addUser(currentUser);
				setNewUser(false);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("User successfully registered."));

				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Exception during registration."));
				return false;
			}
		}

		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No User to be registred found."));
			return false;
		}
	}

	/**
	 * Tries to add the currently selected {@link Vendor} as a new
	 * {@link Vendor} to the DB.
	 */
	public boolean createVendor() {
		if (isNewVendor && canCreateNewVendor()) {
			try {
				vendorManager.addVendor(selectedVendor);
				vendorManager.addCompanyMember(selectedCompanyMember);
				setNewVendor(false);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Company successfully created."));

				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during creation of new company."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new company."));
			return false;
		}
	}

	/**
	 * Tries to add the currently selected {@link Version} as a new
	 * {@link Version} to the DB.
	 */
	public boolean createVersion() {
		if (isNewVersion && canCreateNewVersion()) {
			try {
				versionManager.addVersion(selectedVersion);
				setNewVersion(false);

				Set<Version> appVersions = selectedApp.getVersions();
				appVersions.add(selectedVersion);
				selectedApp.setVersions(appVersions);

				triggerBenchmarking(selectedVersion);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Version successfully created."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during creation of app version."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new version for this app."));
			return false;
		}
	}

	/**
	 * Removes the {@link Binary} APK file from a given {@link TestSuite} and
	 * deletes the {@link Binary} in the DB.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite}.
	 * @return Whether or not the {@link Binary} has been deleted successfully.
	 */
	public boolean deleteTestSuiteApk(TestSuite testSuite) {

		if (canEditTestSuite(testSuite)) {
			testSuiteManager.deleteApkFromTestSuite(testSuite);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("APK file successfully deleted."));
			return true;
		} else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have permission to remove the APK from the test suite."));
			return false;
		}
	}

	/**
	 * Removes a given {@link TestRun} from the DB.
	 * 
	 * @param testRun
	 *            The {@link TestRun}.
	 * @return Whether or not the {@link TestRun} has been deleted successfully.
	 */
	public boolean deleteTestRun(TestRun testRun) {

		if (canDeleteTestRun(testRun)) {
			testSuiteManager.deleteTestRunFromTestSuite(testRun);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("TestRun successfully deleted."));
			return true;
		} else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have permission to remove the test run."));
			return false;
		}
	}

	/**
	 * Returns the current {@link User} if logged in.
	 * 
	 * @return The current {@link User} if logged in.
	 */
	@Produces
	@LoggedIn
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Returns all {@link Type}s the gender setting can have.
	 * 
	 * @return All {@link Type}s the gender setting can have.
	 */
	public List<Type> getGenderTypes() {
		Type genderType;
		try {
			genderType = typeManager.getType(TypeConstants.GENDER_TYPE);

			if (null != genderType) {
				List<Type> result = typeManager.getSubTypes(genderType);

				if (result.size() > 0)
					return result;
				else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("No gender types found."));
					return Collections.emptyList();
				}
			}

			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Gender Type not found in DB."));
				return Collections.emptyList();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Exception during fetching gener types."));
			return Collections.emptyList();
		}
	}

	/**
	 * Returns the email address of a {@link User} that shall be invited as a
	 * {@link CompanyMember} for the currently selected {@link Vendor} or as a
	 * {@link Member} for the currently selected {@link App}.
	 * 
	 * @return The email address of a {@link User} that shall be invited as a
	 *         {@link CompanyMember} for the currently selected {@link Vendor}
	 *         or as a {@link Member} for the currently selected {@link App}.
	 */
	public String getNewMemberMail() {
		return newMemberMail;
	}

	/**
	 * Returns all {@link PaymentPlan}s the currently logged in {@link User} can
	 * select for an {@link App}, {@link Vendor} etc.
	 * 
	 * @return All {@link PaymentPlan}s the currently logged in {@link User} can
	 *         select for an {@link App}, {@link Vendor} etc.
	 */
	public List<PaymentPlan> getPaymentPlans() {
		/* Currently returns all existing payment plans. */
		try {
			return paymentPlanManager.getPaymentPlans();
		}

		catch (Exception e) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"Error during retrieval of payment plans."));
			return Collections.emptyList();
		}
	}

	/**
	 * Returns the currently selected {@link App} (project) to be displayed in
	 * the account.
	 * 
	 * @return The currently selected {@link App} (project) to be displayed in
	 *         the account.
	 */
	public App getSelectedApp() {
		return selectedApp;
	}

	/**
	 * Returns the currently selected {@link CompanyMember} to be displayed in
	 * the account.
	 * 
	 * @return The currently selected {@link CompanyMember} to be displayed in
	 *         the account.
	 */
	public CompanyMember getSelectedCompanyMember() {
		return selectedCompanyMember;
	}

	/**
	 * Returns the currently selected {@link Member} belonging to the currently
	 * selected {@link App}.
	 * 
	 * @return The currently selected {@link Member} belonging to the currently
	 *         selected {@link App}.
	 */
	public Member getSelecteMember() {
		return selecteMember;
	}

	/**
	 * Returns the currently selected {@link TestCase} to be displayed in the
	 * account.
	 * 
	 * @return The currently selected {@link TestCase} to be displayed in the
	 *         account.
	 */
	public TestCase getSelectedTestCase() {
		return selectedTestCase;
	}

	/**
	 * Returns the currently selected {@link TestRun} to be displayed in the
	 * account.
	 * 
	 * @return The currently selected {@link TestRun} to be displayed in the
	 *         account.
	 */
	public TestRun getSelectedTestRun() {
		return selectedTestRun;
	}

	/**
	 * Returns the currently selected {@link TestSuite} to be displayed in the
	 * account.
	 * 
	 * @return The currently selected {@link TestSuite} to be displayed in the
	 *         account.
	 */
	public TestSuite getSelectedTestSuite() {
		return selectedTestSuite;
	}

	/**
	 * Returns the currently selected {@link Vendor} (project) to be displayed
	 * in the account.
	 * 
	 * @return The currently selected {@link Vendor} (project) to be displayed
	 *         in the account.
	 */
	public Vendor getSelectedVendor() {
		return selectedVendor;
	}

	/**
	 * Returns the currently selected {@link Version} to be displayed in the
	 * account.
	 * 
	 * @return The currently selected {@link Version} to be displayed in the
	 *         account.
	 */
	public Version getSelectedVersion() {
		return selectedVersion;
	}
	
	public String getMeasurementResultsForTestRun(TestRun run) {
		Map<Long,Float> data = new HashMap<Long,Float>();
		String ret = "$(function () {$('#results').highcharts({chart: {type: 'line'},title: {text: 'Power Measurements'},xAxis: {type: 'datetime'},yAxis: {title: {text: 'Power [mW]'}},series: [{name:'Power', data:";
		ret += "[";
		try {
			data = testSuiteManager.getMeasurementResultsForTestRun(run);
			Set<Long> keys = data.keySet();
			TreeSet<Long> ts = new TreeSet<Long>(keys);
			for(Long key : ts) {
				Float val = data.get(key);
				if(val < 0) val *= -1;
				ret += "["+key+", "+val+"], ";
			}
			if(ret.endsWith(", ")) {
				ret = ret.substring(0,ret.length()-2);
			}
			ret += "],";
			ret+="lineWidth: 1}]});});";
			return ret;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No Measurement Results for TestRun."));
			return "";
		}
		
	}

	/**
	 * Returns all {@link Type}s a {@link TestRun} can have.
	 * 
	 * @return All {@link Type}s a {@link TestRun} can have.
	 */
	public List<Type> getTestRunTypes() {
		Type genderType;
		try {
			genderType = typeManager.getType(TypeConstants.TESTRUN_TYPE);

			if (null != genderType) {
				List<Type> result = typeManager.getSubTypes(genderType);

				if (result.size() > 0)
					return result;
				else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("No test run types found."));
					return Collections.emptyList();
				}
			}

			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("TestRun Type not found in DB."));
				return Collections.emptyList();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(
							"Exception during fetching test run types."));
			return Collections.emptyList();
		}
	}

	/**
	 * Returns all {@link Vendor}s, that can be selected for a given {@link App}
	 * to be set as its {@link Vendor} (includes the current {@link Vendor} of
	 * the {@link App} and {@link Vendor}s with valid {@link CompanyMember}ship
	 * of the currently logged in {@link User}.
	 * 
	 * @param app
	 *            The {@link App}.
	 * @return All {@link Vendor}s, that can be selected for a given {@link App}
	 *         to be set as its {@link Vendor}.
	 */
	public List<Vendor> getValidVendorsForApp(App app) {

		List<CompanyMember> memberships;
		try {
			memberships = userManager.getCompanyMembers(getCurrentUser());

			List<Vendor> result = new ArrayList<Vendor>(memberships.size() + 1);
			for (CompanyMember member : memberships) {
				if (member.getType().isOfType(
						TypeConstants.COMPANY_MEMBER_TYPE_COMPANY_OWNER)
						|| member.getType().isOfType(
								TypeConstants.COMPANY_MEMBER_TYPE_MEMBER))
					result.add(member.getVendor());
				// no else.s
			}
			// end for.

			if (null != app.getVendor() && !result.contains(app.getVendor()))
				result.add(app.getVendor());
			// no else.

			return result;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error during retrieval of companies."));
			return Collections.emptyList();
		}
	}

	/**
	 * Returns all {@link Type}s the {@link Version} visibility setting can
	 * have.
	 * 
	 * @return All {@link Type}s the {@link Version} visibility setting can
	 *         have.
	 */
	public List<Type> getVersionTypes() {
		Type genderType;
		try {
			genderType = typeManager.getType(TypeConstants.VERSION_TYPE);

			if (null != genderType) {
				List<Type> result = typeManager.getSubTypes(genderType);

				if (result.size() > 0)
					return result;
				else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("No version types found."));
					return Collections.emptyList();
				}
			}

			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Version Type not found in DB."));
				return Collections.emptyList();
			}
		} catch (Exception e) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"Exception during fetching version types."));
			return Collections.emptyList();
		}
	}

	/**
	 * Returns whether the {@link User} of this {@link Login} is currently
	 * logged in.
	 * 
	 * @return If <code>true</code> the {@link User} is logged in.
	 */
	public boolean isLoggedIn() {
		return currentUser != null && null != currentUser.getStatus()
				&& (currentUser.getStatus().isOfType("active") ||
				    currentUser.getStatus().isOfType("admin"));
	}
	
	public boolean isAdmin() {
		return currentUser != null && null != currentUser.getStatus() &&
				currentUser.getStatus().isOfType("admin");
	}
	
	public void setAdmin(boolean x) {
		try {
			currentUser.setStatus(typeManager.getType("admin"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns whether the currently edited {@link App} is a new {@link App} not
	 * yet persisted to the DB.
	 * 
	 * @return Whether the currently edited {@link App} is a new {@link App} not
	 *         yet persisted to the DB.
	 */
	public boolean isNewApp() {
		return isNewApp;
	}

	/**
	 * Returns whether the currently edited {@link CompanyMember} is a new
	 * {@link CompanyMember} not yet persisted to the DB.
	 * 
	 * @return Whether the currently edited {@link CompanyMember} is a new
	 *         {@link CompanyMember} not yet persisted to the DB.
	 */
	public boolean isNewCompanyMember() {
		return isNewCompanyMember;
	}

	/**
	 * Returns whether the currently edited {@link TestRun} is a new
	 * {@link TestRun} not yet persisted to the DB.
	 * 
	 * @return Whether the currently edited {@link TestRun} is a new
	 *         {@link TestRun} not yet persisted to the DB.
	 */
	public boolean isNewTestRun() {
		return isNewTestRun;
	}

	/**
	 * Returns whether the currently edited {@link TestSuite} is a new
	 * {@link TestSuite} not yet persisted to the DB.
	 * 
	 * @return Whether the currently edited {@link TestSuite} is a new
	 *         {@link TestSuite} not yet persisted to the DB.
	 */
	public boolean isNewTestSuite() {
		return isNewTestSuite;
	}

	/**
	 * Returns whether the current {@link User} is already registered or is new.
	 * 
	 * @return Whether the current {@link User} is already registered or is new.
	 */
	public boolean isNewUser() {
		return isNewUser;
	}

	/**
	 * Returns whether the currently edited {@link Vendor} is a new
	 * {@link Vendor} not yet persisted to the DB
	 * 
	 * @return Whether the currently edited {@link Vendor} is a new
	 *         {@link Vendor} not yet persisted to the DB
	 */
	public boolean isNewVendor() {
		return isNewVendor;
	}

	/**
	 * Returns whether the currently edited {@link Version} is a new
	 * {@link Version} not yet persisted to the DB.
	 * 
	 * @return Whether the currently edited {@link Version} is a new
	 *         {@link Version} not yet persisted to the DB.
	 */
	public boolean isNewVersion() {
		return isNewVersion;
	}

	/**
	 * Logs the current {@link UserException} in.
	 * 
	 * @throws Exception
	 */
	public boolean logIn() throws Exception {

		User user = userManager.findUser(credentials.getUsername(),
				credentials.getPassword());

		if (user != null) {
			this.currentUser = user;
			setNewUser(false);
			if(this.currentUser.getStatus().getName().equals("admin")) {
				setAdmin(true);
			}
			return true;
		}

		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Invalid login or password."));
			return false;
		}
	}

	/**
	 * Logs the current {@link User} out.
	 */
	public boolean logOut() {
		resetFields();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String redirect = "logout";
		NavigationHandler myNav = facesContext.getApplication()
				.getNavigationHandler();
		myNav.handleNavigation(facesContext, null, redirect);

		facesContext.addMessage(null, new FacesMessage(
				"Goodbye. You were successfully logged out."));

		return true;
	}

	/**
	 * Sets the currently selected {@link App} (project) and triggers the
	 * opening of the app detail page.
	 * 
	 * @param appToBeOpened
	 *            The {@link App} to be opened.
	 * @param member
	 *            The {@link Member} relating the current {@link User} to the
	 *            given {@link App}. Can be <code>null</code>.
	 */
	public boolean openApp(App appToBeOpened, Member member) {

		if (canViewApp(appToBeOpened, null)) {
			setSelectedApp(appToBeOpened);
			setNewApp(false);
			setSelecteMember(member);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "project";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to view the settings of this app."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link App} (project) as a newly created
	 * {@link App} and triggers the opening of the app detail page.
	 */
	public boolean openNewApp() {

		if (canCreateNewApp()) {
			setSelectedApp(new App());
			setNewApp(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "project";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);
			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new app."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link TestRun} as a newly created
	 * {@link TestRun} and triggers the opening of the {@link TestRun} detail
	 * page.
	 */
	public boolean openNewTestRun() {

		if (canTriggerTestRun(getSelectedTestSuite())) {
			setSelectedTestRun(new TestRun());
			selectedTestRun.setTestSuite(getSelectedTestSuite());
			selectedTestRun.setIdleTime(0l);
			selectedTestRun.setNumberOfRuns(1l);
			selectedTestRun.setTestScript(selectedTestRun.getTestSuite()
					.getTestScript());
			try {
				selectedTestRun.setType(typeManager
						.getType(TypeConstants.TESTRUN_TYPE_SCHEDULED));
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Unable to fetch test run type from DB."));
			}
			setNewTestRun(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "testrun";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to trigger a new test run for this test suite."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link TestSuite} as a newly created
	 * {@link TestSuite} and triggers the opening of the {@link TestSuite}
	 * detail page.
	 */
	public boolean openNewTestSuite() {

		if (canCreateNewTestSuite()) {
			setSelectedTestSuite(new TestSuite());
			selectedTestSuite.setVersion(getSelectedVersion());
			setNewTestSuite(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "testsuite";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new test suite for this app."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link Vendor} as a newly created
	 * {@link Vendor} and triggers the opening of the {@link Vendor} detail
	 * page.
	 */
	public boolean openNewVendor() {

		if (canCreateNewVendor()) {
			setSelectedVendor(new Vendor());
			setSelectedCompanyMember(new CompanyMember());
			selectedCompanyMember.setUserMetaData(getCurrentUser()
					.getUserMetaData());
			selectedCompanyMember.setVendor(getSelectedVendor());

			try {
				selectedCompanyMember
						.setType(typeManager
								.getType(TypeConstants.COMPANY_MEMBER_TYPE_COMPANY_OWNER));
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Error during fetching company owner type."));
				return false;
			}

			setNewVendor(true);
			setNewCompanyMember(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "company";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new company."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link Version} as a newly created
	 * {@link Version} and triggers the opening of the {@link Version} detail
	 * page.
	 */
	public boolean openNewVersion() {

		if (canCreateNewVersion()) {
			setSelectedVersion(new Version());
			selectedVersion.setApp(getSelectedApp());
			setNewVersion(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "version";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new version for this app."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link User} as a newly created {@link User}
	 * and triggers the opening of the user detail page (register).
	 */
	public boolean openNewUser() {
		currentUser = (new User());
		currentUser.setUserMetaData(new UserMetaData());
		setNewUser(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String redirect = "settings";
		NavigationHandler myNav = facesContext.getApplication()
				.getNavigationHandler();
		myNav.handleNavigation(facesContext, null, redirect);
		return true;
	}

	/**
	 * Sets the currently selected {@link TestCase} and triggers the opening of
	 * the {@link TestCase} detail page.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be opened.
	 */
	public boolean openTestCase(TestCase testCase) {

		if (canViewTestCase(testCase)) {
			setSelectedTestCase(testCase);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "testcase";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to open a test run for this test suite."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link TestRun} and triggers the opening of
	 * the {@link TestRun} detail page.
	 * 
	 * @param testRun
	 *            The {@link TestRun} to be opened.
	 */
	public boolean openTestRun(TestRun testRun) {

		if (canViewTestRun(testRun)) {
			setSelectedTestRun(testRun);
			setNewTestRun(false);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "testrun";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to open a test run for this test suite."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link TestSuite} and triggers the opening of
	 * the {@link TestSuite} detail page.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be opened.
	 */
	public boolean openTestSuite(TestSuite testSuite) {

		if (canViewTestSuite(testSuite)) {
			setSelectedTestSuite(testSuite);
			setNewTestSuite(false);
			setNewTestRun(false);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "testsuite";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to open a test suite for this version."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link Vendor} and triggers the opening of
	 * the {@link Vendor} detail page.
	 * 
	 * @param vendor
	 *            The {@link Vendor} to be opened.
	 * @param member
	 *            The {@link CompanyMember} that wants to open the
	 *            {@link Vendor}.
	 */
	public boolean openVendor(Vendor vendor, CompanyMember member) {
		if (canViewVendor(vendor)) {
			setSelectedVendor(vendor);
			setSelectedCompanyMember(member);
			setNewVendor(false);
			setNewCompanyMember(false);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "company";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to view the settings of this company."));
			return false;
		}
	}

	/**
	 * Sets the currently selected {@link Version} and triggers the opening of
	 * the {@link Version} detail page.
	 * 
	 * @param version
	 *            The {@link Version} to be opened.
	 */
	public boolean openVersion(Version version) {

		if (canViewVersion(version)) {
			setSelectedVersion(version);
			setNewVersion(false);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String redirect = "version";
			NavigationHandler myNav = facesContext.getApplication()
					.getNavigationHandler();
			myNav.handleNavigation(facesContext, null, redirect);

			return true;
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to open a version for this app."));
			return false;
		}
	}

	/**
	 * Sets whether the currently edited {@link App} is a new {@link App} not
	 * yet persisted to the DB.
	 * 
	 * @param isNewApp
	 *            Whether the currently edited {@link App} is a new {@link App}
	 *            not yet persisted to the DB.
	 */
	public void setNewApp(boolean isNewApp) {
		this.isNewApp = isNewApp;
	}

	/**
	 * Sets whether the currently edited {@link CompanyMember} is a new
	 * {@link CompanyMember} not yet persisted to the DB.
	 * 
	 * @param isNewCompanyMember
	 *            Whether the currently edited {@link CompanyMember} is a new
	 *            {@link CompanyMember} not yet persisted to the DB.
	 */
	public void setNewCompanyMember(boolean isNewCompanyMember) {
		this.isNewCompanyMember = isNewCompanyMember;
	}

	/**
	 * Sets the email address of a {@link User} that shall be invited as a
	 * {@link CompanyMember} for the currently selected {@link Vendor} or as a
	 * {@link Member} for the currently selected {@link App}.
	 * 
	 * @param newMemberMail
	 *            The email address of a {@link User} that shall be invited as a
	 *            {@link CompanyMember} for the currently selected
	 *            {@link Vendor} or as a {@link Member} for the currently
	 *            selected {@link App}.
	 */
	public void setNewMemberMail(String newMemberMail) {
		this.newMemberMail = newMemberMail;
	}

	/**
	 * Sets whether the currently edited {@link TestRun} is a new
	 * {@link TestRun} not yet persisted to the DB.
	 * 
	 * @param isNewTestRun
	 *            Whether the currently edited {@link TestRun} is a new
	 *            {@link TestRun} not yet persisted to the DB.
	 */
	public void setNewTestRun(boolean isNewTestRun) {
		this.isNewTestRun = isNewTestRun;
	}

	/**
	 * Sets whether the currently edited {@link TestSuite} is a new
	 * {@link TestSuite} not yet persisted to the DB.
	 * 
	 * @param isNewTestSuite
	 *            Whether the currently edited {@link TestSuite} is a new
	 *            {@link TestSuite} not yet persisted to the DB.
	 */
	public void setNewTestSuite(boolean isNewTestSuite) {
		this.isNewTestSuite = isNewTestSuite;
	}

	/**
	 * Sets whether the current {@link User} is already registered or is new.
	 * 
	 * @param isNewUser
	 *            Whether the current {@link User} is already registered or is
	 *            new.
	 */
	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	/**
	 * Sets whether the currently edited {@link Vendor} is a new {@link Vendor}
	 * not yet persisted to the DB
	 * 
	 * @param isNewVendor
	 *            Whether the currently edited {@link Vendor} is a new
	 *            {@link Vendor} not yet persisted to the DB
	 */
	public void setNewVendor(boolean isNewVendor) {
		this.isNewVendor = isNewVendor;
	}

	/**
	 * Sets whether the currently edited {@link Version} is a new
	 * {@link Version} not yet persisted to the DB.
	 * 
	 * @param isNewVersion
	 *            Whether the currently edited {@link Version} is a new
	 *            {@link Version} not yet persisted to the DB.
	 */
	public void setNewVersion(boolean isNewVersion) {
		this.isNewVersion = isNewVersion;
	}

	/**
	 * Sets the currently selected {@link App} (project) to be displayed in the
	 * account.
	 * 
	 * @param selectedApp
	 *            The currently selected {@link App} (project) to be displayed
	 *            in the account.
	 */
	public void setSelectedApp(App selectedApp) {
		this.selectedApp = selectedApp;
	}

	/**
	 * Sets the currently selected {@link CompanyMember} to be displayed in the
	 * account.
	 * 
	 * @param selectedCompanyMember
	 *            The currently selected {@link CompanyMember} to be displayed
	 *            in the account.
	 */
	public void setSelectedCompanyMember(CompanyMember selectedCompanyMember) {
		this.selectedCompanyMember = selectedCompanyMember;
	}

	/**
	 * Sets the currently selected {@link Member} belonging to the currently
	 * selected {@link App}.
	 * 
	 * @param selecteMember
	 *            The currently selected {@link Member} belonging to the
	 *            currently selected {@link App}.
	 */
	public void setSelecteMember(Member selecteMember) {
		this.selecteMember = selecteMember;
	}

	/**
	 * Sets the currently selected {@link TestCase} to be displayed in the
	 * account.
	 * 
	 * @param testCase
	 *            The currently selected {@link TestCase} to be displayed in the
	 *            account.
	 */
	public void setSelectedTestCase(TestCase testCase) {
		this.selectedTestCase = testCase;
	}

	/**
	 * Sets the currently selected {@link TestRun} to be displayed in the
	 * account.
	 * 
	 * @param testRun
	 *            The currently selected {@link TestRun} to be displayed in the
	 *            account.
	 */
	public void setSelectedTestRun(TestRun testRun) {
		this.selectedTestRun = testRun;
	}

	/**
	 * Sets the currently selected {@link TestSuite} to be displayed in the
	 * account.
	 * 
	 * @param selectedTestSuite
	 *            The currently selected {@link TestSuite} to be displayed in
	 *            the account.
	 */
	public void setSelectedTestSuite(TestSuite selectedTestSuite) {
		this.selectedTestSuite = selectedTestSuite;
	}

	/**
	 * Sets the currently selected {@link Vendor} (project) to be displayed in
	 * the account.
	 * 
	 * @param selectedVendor
	 *            The currently selected {@link Vendor} (project) to be
	 *            displayed in the account.
	 */
	public void setSelectedVendor(Vendor selectedVendor) {
		this.selectedVendor = selectedVendor;
	}

	/**
	 * Sets the currently selected {@link Version} to be displayed in the
	 * account.
	 * 
	 * @param selectedVersion
	 *            The currently selected {@link Version} to be displayed in the
	 *            account.
	 */
	public void setSelectedVersion(Version selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	/** TODO */
	private int jobCounter = 0;

	/**
	 * Tries to add the currently selected {@link TestRun} as a new
	 * {@link TestSuite} to the DB and triggers its execution.
	 */
	public boolean triggerTestRun() {
		if (isNewTestRun()
				&& canTriggerTestRun(getSelectedTestRun().getTestSuite())) {
			try {
				testSuiteManager.addTestRun(getSelectedTestRun());
				setNewTestRun(false);

				/*
				 * http://www.mastertheboss.com/jboss-quartz/quartz-2-tutorial-on
				 * -jboss-as-7
				 */
				// TODO Trigger test run
				try {
					SchedulerFactory sf = new StdSchedulerFactory();
					Scheduler sched = sf.getScheduler();
					sched.start();

					Date runTime = new Date();
					// Trigger the job to run on the next round minute
					Trigger trigger = newTrigger()
							.withIdentity("trigger1", "group1")
							.startAt(runTime).build();
					// Define job instance
					jobCounter++;
					JobDetail job1 = newJob(ProfilingJob.class)
							.withIdentity("job" + jobCounter, "group1")
							.usingJobData("testRunID",
									getSelectedTestRun().getTestRunID())
							.build();

					// Schedule the job with the trigger
					sched.scheduleJob(job1, trigger);

				} catch (Exception exc) {
					exc.printStackTrace();
				}

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Test run successfully created and triggered."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Error during creation of test run."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to create a new test run for this test suite."));
			return false;
		}
	}

	/**
	 * Updates a given {@link App} in the DB.
	 * 
	 * @param app
	 *            The {@link App} to be updated.
	 * @return <code>true</code> if updated successfully.
	 */
	public boolean updateApp(App app) {

		if (canEditApp(app, null)) {
			try {
				appManager.updateApp(selectedApp);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("App successfully updated."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Error during update of app."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to edit this app."));
			return false;
		}
	}

	/**
	 * Updates a given {@link TestSuite} in the DB.
	 * 
	 * @param testSuite
	 *            The {@link TestSuite} to be updated.
	 * @return <code>true</code> if updated successfully.
	 */
	public boolean updateTestSuite(TestSuite testSuite) {

		if (canEditTestSuite(testSuite)) {
			try {
				testSuiteManager.updateTestSuite(getSelectedTestSuite());

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Test suite successfully updated."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Error during update of test suite."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to edit a test suite of this version."));
			return false;
		}
	}

	/**
	 * Tries to update the current {@link User} in the DB.
	 */
	public boolean updateUser() {

		if (canEditSettings(getCurrentUser())) {
			try {
				userManager.updateUser(currentUser);
				setNewUser(false);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"The settings were successfully updated."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Exception during settings update."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to alter the settings of this user."));
			return false;
		}
	}

	/**
	 * Tries to update the currently selected {@link Vendor}in the DB.
	 */
	public boolean updateVendor() {
		if (canEditVendor(getSelectedVendor())) {
			try {
				vendorManager.updateVendor(getSelectedVendor());
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Company successfully updated."));
				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Error during update of company."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to alter the settings of this company."));
			return false;
		}
	}

	/**
	 * Updates a given {@link Version} in the DB.
	 * 
	 * @param version
	 *            The {@link Version} to be updated.
	 * @return <code>true</code> if updated successfully.
	 */
	public boolean updateVersion(Version version) {

		if (canEditVersion(version)) {
			try {
				versionManager.updateVersion(getSelectedVersion());

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("App version successfully updated."));

				/* Probably trigger benchmark if version is released. */
				triggerBenchmarking(version);

				return true;
			}

			catch (Exception e) {
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										"Error during update of app version."));
				return false;
			}
		}

		else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									"You do not have the permission to edit a version of this app."));
			return false;
		}
	}

	/**
	 * Helper method checking whether a given {@link Version} has no valid
	 * {@link EnergyModel} yet.
	 * 
	 * @param version
	 *            The {@link Version} to be checked.
	 * @return <code>true</code> if no or only an empty {@link EnergyModel}
	 *         exists.
	 */
	private boolean hasEmptyEnergyModel(Version version) {
		if (null == version.getEnergyModel())
			return true;
		else {
			if (null == version.getEnergyModel().getPowerRates())
				return true;
			else {
				for (PowerRate rate : version.getEnergyModel().getPowerRates()) {
					if (null != rate.getValue())
						return false;
					// no else.
				}
				// end for.

				/* No valid power rate found. */
				return true;
			}
		}
	}

	/**
	 * Helper method resetting the fields of this {@link Account} once logged
	 * out.
	 * 
	 * @return <code>true</code> if fields were reset successfully.
	 */
	private boolean resetFields() {
		currentUser = null;
		isNewApp = false;
		isNewCompanyMember = false;
		isNewTestRun = false;
		isNewTestSuite = false;
		isNewUser = false;
		isNewVendor = false;
		isNewVersion = false;
		newMemberMail = null;
		selectedApp = null;
		selectedCompanyMember = null;
		selecteMember = null;
		selectedTestCase = null;
		selectedTestRun = null;
		selectedTestSuite = null;
		selectedVendor = null;
		selectedVersion = null;

		return true;
	}

	/**
	 * Helper method triggering the energy benchmarking of a given
	 * {@link Version}.
	 * 
	 * @param version
	 *            The {@link Version} to be benchmarked.
	 * @throws Exception
	 */
	private void triggerBenchmarking(Version version) throws Exception {
		// check if version is released
		if (version.getVisibility().isOfType(
				TypeConstants.VERSION_TYPE_RELEASED)
				&& null != version.getApk()) {

			// check if energy model exists
			if (hasEmptyEnergyModel(version)) {

				TestSuite benchmarkSuite = null;

				// check if benchmark suite exists
				for (TestSuite testSuite : version.getTestSuites()) {
					if (testSuite.getName().equals(
							BenchmarkUtil.BENCHMARK_TESTSUITE_NAME)) {
						benchmarkSuite = testSuite;
						break;
					}
					// no else.
				}
				// end for.

				// if not: create benchmark suite
				if (null == benchmarkSuite) {
					benchmarkSuite = new TestSuite();
					benchmarkSuite.setVersion(version);
					benchmarkSuite
							.setName(BenchmarkUtil.BENCHMARK_TESTSUITE_NAME);

					String testScript = BenchmarkUtil.BENCHMARK_TESTSCRIPT_WALLPAPER;
					testScript = testScript
							.replace(
									BenchmarkUtil.BENCHMARK_TESTSCRIPT_APK_NAME_PLACEHOLDER,
									version.getApk()
											.getName()
											.substring(
													0,
													version.getApk().getName()
															.length() - 4))
							.replace(
									BenchmarkUtil.BENCHMARK_TESTSCRIPT_APPID_PLACEHOLDER,
									version.getApp().getPackageID());
					/*
					 * Alter number of curser down statements depending on app
					 * name
					 */
					String[] installedWallPapers = { "Bubbles", "Holo Special",
							"Nexus", "Phase Beam", "Spektrum", "Wasser" };
					StringBuffer replacement = new StringBuffer();
					replacement
							.append(BenchmarkUtil.BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_STATEMENT);
					for (String wallpaperName : installedWallPapers) {
						if (wallpaperName.compareTo(version.getApp().getName()) < 0)
							replacement
									.append(BenchmarkUtil.BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_STATEMENT);
						else
							break;
					}
					// end for.

					testScript = testScript
							.replace(
									BenchmarkUtil.BENCHMARK_TESTCRIPT_WALLPAPER_APP_SELECTION_PLACEHOLDER,
									replacement.toString());

					benchmarkSuite.setTestScript(testScript);
					testSuiteManager.addTestSuite(benchmarkSuite);

					Set<TestSuite> testSuites = getSelectedVersion()
							.getTestSuites();
					testSuites.add(benchmarkSuite);
					getSelectedVersion().setTestSuites(testSuites);
				}
				// no else.

				// Trigger test run of benchmark suite.
				TestRun testRun = new TestRun();
				/* TODO extract device selection. */
				testRun.setDevice(deviceManager
						.getDevice("Samsung Galaxy Nexus"));
				testRun.setIdleTime(0l);
				try {
					testRun.setType(typeManager
							.getType(TypeConstants.TESTRUN_TYPE_SCHEDULED));
				} catch (Exception e) {
					/* TODO Handle exception. */
				}
				testRun.setIsHwProfilingEnabled(false);
				testRun.setNumberOfRuns(1l);
				testRun.setTestScript(benchmarkSuite.getTestScript());
				testRun.setTestSuite(benchmarkSuite);
				setSelectedTestRun(testRun);
				setNewTestRun(true);
				triggerTestRun();

				setNewTestRun(false);
				testRun = testSuiteManager.getTestRun(testRun.getTestRunID());
				openTestRun(testRun);
			}
		}
	}
}