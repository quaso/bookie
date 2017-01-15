package org.bookie.repository.test;

import java.util.Optional;

import org.bookie.model.Organization;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationRepositoryTest extends AbstractTest {

	@Test
	public void testCreate() {
		final Organization org = new Organization();
		org.setName("name");
		org.setCode("code");
		this.organizationRepository.save(org);
		Assert.assertNotNull(org.getId());

		final Optional<Organization> dbOrg = this.organizationRepository.findByName(org.getName());
		Assert.assertTrue(dbOrg.isPresent());
		Assert.assertEquals(org.getId(), dbOrg.get().getId());
	}

	@Test
	public void testCreateUniqueName() {
		final Organization org = new Organization();
		org.setName("name");
		org.setCode("code");
		this.organizationRepository.save(org);

		this.organizationRepository.findAll();
		try {
			final Organization org2 = new Organization();
			org2.setName("name");
			org2.setCode(org.getCode()+"XXX");
			this.organizationRepository.save(org2);
			this.organizationRepository.findAll();
			Assert.fail("Unique constraint for name/organization failed");
		} catch (final DataIntegrityViolationException ex) {
		}
	}

	@Test
	public void testCreateUniqueCode() {
		final Organization org = new Organization();
		org.setName("name");
		org.setCode("code");
		this.organizationRepository.save(org);

		this.organizationRepository.findAll();
		try {
			final Organization org2 = new Organization();
			org2.setName(org.getName()+"XXXX");
			org2.setCode("code");
			this.organizationRepository.save(org2);
			this.organizationRepository.findAll();
			Assert.fail("Unique constraint for name/organization failed");
		} catch (final DataIntegrityViolationException ex) {
		}
	}
}
