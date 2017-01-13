package org.bookie.repository;

import java.util.Set;

import javax.transaction.Transactional;

import org.bookie.model.OrganizationUserRole;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface OrganizationUserRoleRepository extends CrudRepository<OrganizationUserRole, String> {

	public Set<OrganizationUserRole> getByUserIdAndOrganizationName(String userId, String organizationName);

	public void deleteByUserIdAndRoleNameAndOrganizationName(String userId, String roleName, String organizationName);

}
