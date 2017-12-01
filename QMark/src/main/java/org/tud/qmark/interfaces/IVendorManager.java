package org.tud.qmark.interfaces;

import java.util.List;
import java.util.Map;

import org.tud.qmark.entities.CompanyMember;
import org.tud.qmark.entities.Vendor;
import org.tud.qmark.util.VendorConverter;

/**
 * Manages {@link Vendor} entities.
 * 
 * @author Claas Wilke
 */
public interface IVendorManager {

	/**
	 * Adds a new {@link CompanyMember}.
	 * 
	 * @param companyMember
	 *            The {@link CompanyMember} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addCompanyMember(CompanyMember companyMember)
			throws Exception;

	/**
	 * Adds a new {@link Vendor}.
	 * 
	 * @param vendor
	 *            The {@link Vendor} to be addded.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String addVendor(Vendor vendor) throws Exception;

	/**
	 * Returns all {@link CompanyMember}s of a given {@link Vendor}.
	 * 
	 * @param vendor
	 *            The {@link Vendor}.
	 * @return A {@link List} of the {@link Vendor}'s {@link CompanyMember}s.
	 * @throws Exception
	 */
	public List<CompanyMember> getCompanyMembers(Vendor vendor)
			throws Exception;

	/**
	 * Tries to retrieve a {@link Vendor} for the given name.
	 * 
	 * @param name
	 *            The name of the {@link Vendor}.
	 * @return The resolved {@link Vendor} or <code>null</code>.
	 * @throws Exception
	 */
	public Vendor getVendor(String id) throws Exception;

	/**
	 * Returns all existing {@link Vendor}s.
	 * 
	 * @return All existing {@link Vendor}s.
	 * @throws Exception
	 */
	public List<Vendor> getVendors() throws Exception;

	/**
	 * Returns all existing {@link Vendor}s in a map identified by their name.
	 * 
	 * @return All existing {@link Vendor}s in a map identified by their name.
	 * @throws Exception
	 */
	public Map<String, Vendor> getVendorsByName() throws Exception;

	/**
	 * Returns a {@link VendorConverter} to convert between {@link Vendor}s and
	 * their IDs.
	 * 
	 * @return A {@link VendorConverter} to convert between {@link Vendor}s and
	 *         their IDs.
	 */
	public VendorConverter getVendorConverter() throws Exception;

	/**
	 * Updates a {@link CompanyMember} in the DB.
	 * 
	 * @param companyMember
	 *            The {@link CompanyMember} to be updated.
	 * @return A message indicating the result of the methods invocation.
	 * @throws Exception
	 */
	public String updateCompanyMember(CompanyMember companyMember)
			throws Exception;

	/**
	 * Updates a given {@link Vendor} in the DB.
	 * 
	 * @param vendor
	 *            The {@link Vendor} to be updated.
	 */
	public void updateVendor(Vendor vendor) throws Exception;
}
