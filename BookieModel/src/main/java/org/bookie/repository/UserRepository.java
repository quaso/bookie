package org.bookie.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.bookie.model.User;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserRepository extends CrudRepository<User, String> {
	public List<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);

	public Optional<User> findByUsernameEqualsIgnoreCase(String username);
}
