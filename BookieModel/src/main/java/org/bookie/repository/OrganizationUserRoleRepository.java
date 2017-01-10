package org.bookie.repository;

import java.util.Set;

import org.bookie.model.OrganizationUserRole;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationUserRoleRepository extends CrudRepository<OrganizationUserRole, String> {

	public Set<OrganizationUserRole> getByUserIdAndOrganizationName(String userId, String organizationName);

}
