package com.stepup.ecommerce.wallet.service;

import com.stepup.ecommerce.wallet.entity.TransactionType;
import com.stepup.ecommerce.wallet.entity.Wallet;
import com.stepup.ecommerce.wallet.entity.WalletTransaction;
import com.stepup.ecommerce.wallet.repository.WalletRepository;
import com.stepup.ecommerce.wallet.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet getWallet(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));
    }

    @Transactional
    public Wallet deposit(String email, BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Wallet wallet = getWallet(email);

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return wallet;
    }

    @Transactional
    public Wallet withdraw(String email, BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Wallet wallet = getWallet(email);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return wallet;
    }

    public List<WalletTransaction> getTransactions(String email) {

        Wallet wallet = getWallet(email);

        return transactionRepository
                .findByWalletOrderByTimestampDesc(wallet);
    }
}