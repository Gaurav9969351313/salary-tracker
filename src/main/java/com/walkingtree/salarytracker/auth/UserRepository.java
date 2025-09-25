package com.walkingtree.salarytracker.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);

}
