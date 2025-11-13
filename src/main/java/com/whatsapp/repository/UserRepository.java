package com.whatsapp.repository;

import com.whatsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u JOIN u.contacts c WHERE c.id = :userId")
    List<User> findContactsByUserId(Long userId);

    List<User> findByPhoneNumberIn(List<String> phoneNumbers);



    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY u.name ASC")
    List<User> searchUsersByNameOrPhone(@Param("query") String query,
                                        @Param("currentUserId") Long currentUserId);


}
