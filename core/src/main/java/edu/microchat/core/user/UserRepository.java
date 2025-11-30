package edu.microchat.core.user;

import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends ListCrudRepository<User, Long> {
  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);
}
