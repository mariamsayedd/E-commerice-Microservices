package com.stepup.ecommerce.wallet.repository;

import com.stepup.ecommerce.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserEmail(String email);
}