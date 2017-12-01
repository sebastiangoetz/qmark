package org.tud.qmark.util;

import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Member;
import org.tud.qmark.entities.PaymentPlan;
import org.tud.qmark.entities.TestRun;
import org.tud.qmark.entities.Type;
import org.tud.qmark.entities.User;
import org.tud.qmark.entities.Vendor;

/**
 * Contains constants denoting predefined {@link Type}s in the DB.
 * 
 * @author Claas Wilke
 */
public interface TypeConstants {

	/**
	 * Super {@link Type} for all kinds of {@link Member}s.
	 */
	public static final String APP_MEMBER_TYPE = "appMemberType";

	/**
	 * {@link Type} for {@link Member} being owner of an {@link App}.
	 */
	public static final String APP_MEMBER_TYPE_APP_OWNER = "appOwner";

	/**
	 * {@link Type} for {@link Member} being invited as a member of an
	 * {@link App}.
	 */
	public static final String APP_MEMBER_TYPE_INVITED = "invitedAppMember";

	/**
	 * {@link Type} for {@link Member} being member of an {@link App}.
	 */
	public static final String APP_MEMBER_TYPE_MEMBER = "appMember";

	/**
	 * Super {@link Type} for all kinds of {@link CompanyMember}.
	 */
	public static final String COMPANY_MEMBER_TYPE = "companyMemberType";

	/**
	 * {@link Type} for {@link CompanyMember} being owner of a {@link Vendor} .
	 */
	public static final String COMPANY_MEMBER_TYPE_COMPANY_OWNER = "companyOwner";

	/**
	 * {@link Type} for {@link CompanyMember} being invited as a member of a
	 * {@link Vendor} .
	 */
	public static final String COMPANY_MEMBER_TYPE_INVITED = "invitedCompanyMember";

	/**
	 * {@link Type} for {@link CompanyMember} being member of a {@link Vendor} .
	 */
	public static final String COMPANY_MEMBER_TYPE_MEMBER = "companyMember";

	/** Super {@link Type} of all types for the gender of {@link User}s. */
	public static final String GENDER_TYPE = "genderType";

	/** Super {@link Type} of all types for {@link PaymentPlan}s. */
	public static final String PAYMENTPLAN_TYPE = "paymentPlanType";

	/** {@link PaymentPlan} {@link Type} for demo accounts. */
	public static final String PAYMENTPLAN_TYPE_DEMO = "demoPaymentPlan";

	/** Super {@link Type} of all types for {@link TestRun}s. */
	public static final String TESTRUN_TYPE = "testRunType";

	/** A failed {@link TestRun}. */
	public static final String TESTRUN_TYPE_FAILED = "failedTestRun";

	/** A finished {@link TestRun}. */
	public static final String TESTRUN_TYPE_FINISHED = "finishedTestRun";

	/** A running {@link TestRun}. */
	public static final String TESTRUN_TYPE_RUNNING = "runningTestRun";

	/** A scheduled {@link TestRun}. */
	public static final String TESTRUN_TYPE_SCHEDULED = "scheduledTestRun";

	/**
	 * {@link Type} for {@link User}s that have been registered but not yet
	 * approved for login.
	 */
	public static final String USER_TYPE_REGISTERED = "registered";

	/**
	 * {@link Type} for {@link User}s that have been activated but have a demo
	 * account only.
	 */
	public static final String USER_TYPE_DEMO = "demoUser";

	/** Super {@link Type} of all types for the visibility of {@link Version}s. */
	public static final String VERSION_TYPE = "versionType";

	/** Super {@link Type} of all types for the visibility of {@link Version}s. */
	public static final String VERSION_TYPE_RELEASED = "versionReleased";
}
