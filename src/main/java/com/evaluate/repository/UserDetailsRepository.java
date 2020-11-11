package com.evaluate.repository;

import com.evaluate.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    @Query(value = "SELECT * FROM USER_DETAILS ud WHERE ud.USER_EMAIL =:userEmail AND ud.PASSWORD =:userPassword", nativeQuery = true)
    UserDetails validateUser(@Param("userEmail") String userEmail, @Param("userPassword") String password);
}
