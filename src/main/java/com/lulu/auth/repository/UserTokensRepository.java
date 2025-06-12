package com.lulu.auth.repository;

import com.lulu.auth.model.TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokensRepository extends JpaRepository<TokenModel, Long> {
    TokenModel findByUserId(Long UserId);
}
