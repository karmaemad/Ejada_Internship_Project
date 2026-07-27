package com.example.wallet_service.service;

import com.example.wallet_service.entity.*;
import com.example.wallet_service.repository.TransactionRepository;
import com.example.wallet_service.repository.UserRepository;
import com.example.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public BigDecimal getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return wallet.getBalance();
    }

    @Transactional
    public Wallet deposit(String email, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setWallet(savedWallet);
        tx.setCounterpartyEmail(null);
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        return savedWallet;
    }

    @Transactional
    public void transfer(String senderEmail, String recipientEmail, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }
        if (senderEmail.equalsIgnoreCase(recipientEmail)) {
            throw new RuntimeException("Cannot transfer to yourself");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet recipientWallet = walletRepository.findByUserId(recipient.getId())
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        Transaction sentTx = new Transaction();
        sentTx.setType(TransactionType.TRANSFER_SENT);
        sentTx.setAmount(amount);
        sentTx.setWallet(senderWallet);
        sentTx.setCounterpartyEmail(recipientEmail);
        sentTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(sentTx);

        Transaction receivedTx = new Transaction();
        receivedTx.setType(TransactionType.TRANSFER_RECEIVED);
        receivedTx.setAmount(amount);
        receivedTx.setWallet(recipientWallet);
        receivedTx.setCounterpartyEmail(senderEmail);
        receivedTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(receivedTx);

        return;
    }

    public List<Transaction> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return transactionRepository.findByWalletIdOrderByTimestampDesc(wallet.getId());
    }


    @Transactional
    public Wallet withdraw(String email, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdraw amount must be positive");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setType(TransactionType.WITHDRAW);
        tx.setAmount(amount);
        tx.setWallet(savedWallet);
        tx.setCounterpartyEmail(null);
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        return savedWallet;
    }
}