package org.bookie.repository;

import javax.transaction.Transactional;

import org.bookie.model.Role;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface RoleRepository extends CrudRepository<Role, String> {

	public Role findByName(String roleName);

}
