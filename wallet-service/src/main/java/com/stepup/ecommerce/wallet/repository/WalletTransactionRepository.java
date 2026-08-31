package com.stepup.ecommerce.wallet.repository;

import com.stepup.ecommerce.wallet.entity.Wallet;
import com.stepup.ecommerce.wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository

        extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction>

    findByWalletOrderByTimestampDesc(Wallet wallet);
}