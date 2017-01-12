package org.bookie.repository.test;

import org.bookie.model.Organization;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationRepositoryTest extends AbstractTest {

	@Test
	public void testCreate() {
		final Organization org = new Organization();
		org.setName("name");
		this.organizationRepository.save(org);
		Assert.assertNotNull(org.getId());

		final Organization dbOrg = this.organizationRepository.findByName(org.getName());
		Assert.assertNotNull(dbOrg);
		Assert.assertEquals(org.getId(), dbOrg.getId());
	}

	@Test
	public void testCreateUniqueName() {
		final Organization org = new Organization();
		org.setName("name");
		this.organizationRepository.save(org);

		this.organizationRepository.findAll();
		try {
			final Organization org2 = new Organization();
			org2.setName("name");
			this.organizationRepository.save(org2);
			this.organizationRepository.findAll();
			Assert.fail("Unique constraint for name/organization failed");
		} catch (final DataIntegrityViolationException ex) {
		}
	}
}
