package htwb.ai.minh.userservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data Access Layer
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
