package org.bookie.repository;

import java.util.Set;

import javax.transaction.Transactional;

import org.bookie.model.OrganizationUserRole;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface OrganizationUserRoleRepository extends CrudRepository<OrganizationUserRole, String> {

	public Set<OrganizationUserRole> getByUserIdAndOrganizationCode(String userId, String organizationCode);

	public void deleteByUserIdAndRoleNameAndOrganizationCode(String userId, String roleName, String organizationCode);

}
