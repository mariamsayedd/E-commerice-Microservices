package com.stepup.ecommerce.wallet.controller;

import com.stepup.ecommerce.wallet.wallet.AmountRequest;
import com.stepup.ecommerce.wallet.wallet.TransactionResponse;
import com.stepup.ecommerce.wallet.wallet.WalletResponse;
import com.stepup.ecommerce.wallet.entity.Wallet;
import com.stepup.ecommerce.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(Authentication authentication) {
        Wallet wallet = walletService.getWallet(authentication.getName());
        return ResponseEntity.ok(toResponse(wallet));
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @RequestBody AmountRequest request,
            Authentication authentication
    ) {
        Wallet wallet = walletService.deposit(authentication.getName(), request.amount());
        return ResponseEntity.ok(toResponse(wallet));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @RequestBody AmountRequest request,
            Authentication authentication
    ) {
        Wallet wallet = walletService.withdraw(authentication.getName(), request.amount());
        return ResponseEntity.ok(toResponse(wallet));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication) {
        List<TransactionResponse> transactions = walletService.getTransactions(authentication.getName())
                .stream()
                .map(t -> new TransactionResponse(t.getTransactionId(), t.getType(), t.getAmount(), t.getTimestamp()))
                .toList();
        return ResponseEntity.ok(transactions);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(wallet.getWalletId(), wallet.getBalance(), wallet.getCurrency());
    }
}