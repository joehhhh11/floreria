package com.lulu.auth.repository;

import com.lulu.auth.model.TwoFAModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User2FARepository extends JpaRepository<TwoFAModel, Long> {
    TwoFAModel findByUser_Id(Long userId);
}
