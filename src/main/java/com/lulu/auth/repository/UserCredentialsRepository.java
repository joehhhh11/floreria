package com.lulu.auth.repository;

import com.lulu.auth.model.CredentialsModel;
import com.lulu.auth.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<CredentialsModel, Long> {
    CredentialsModel findByUser(UserModel user);
}
