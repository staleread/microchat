package edu.microchat.user;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends ListCrudRepository<User, Long> {
  boolean existsByUsername(String username);
}
