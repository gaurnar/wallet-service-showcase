package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.gsoft.showcase.wallet.domain.TransactionSpecification;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.dto.TransactionRequest;
import org.gsoft.showcase.wallet.error.InvalidTransaction;
import org.gsoft.showcase.wallet.error.WalletInsufficientFundsException;

public class TransactionWalletSynchronizedProcessor implements TransactionProcessor {

    private final WalletRegistry walletRegistry;

    private Map<UUID, TransactionSpecification> processedTransactions;

    public TransactionWalletSynchronizedProcessor(WalletRegistry walletRegistry) {
        this.walletRegistry = walletRegistry;

        processedTransactions = new ConcurrentHashMap<>();
    }

    @Override
    public void process(TransactionRequest transactionRequest) {
        TransactionSpecification transaction =
            new TransactionSpecification(transactionRequest.getId(),
                                         transactionRequest.getFrom(),
                                         transactionRequest.getTo(),
                                         new BigDecimal(transactionRequest.getAmount()));

        TransactionSpecification existingTransaction =
            processedTransactions.putIfAbsent(transaction.getId(), transaction);

        if (existingTransaction != null) {
            if (!existingTransaction.equals(transaction)) {
                throw new InvalidTransaction("different parameters for known transaction");
            }
            return;
        }

        Wallet fromWallet = walletRegistry.get(transaction.getFrom());
        Wallet toWallet = walletRegistry.get(transaction.getTo());

        Wallet[] locks = orderWalletsForLock(fromWallet, toWallet);

        synchronized (locks[0]) {
            synchronized (locks[1]) {
                BigDecimal fromOldBalance = fromWallet.getBalance();
                BigDecimal toOldBalance = toWallet.getBalance();

                if (fromOldBalance.compareTo(transaction.getAmount()) < 0) {
                    throw new WalletInsufficientFundsException();
                }

                BigDecimal fromNewBalance = fromOldBalance.subtract(transaction.getAmount()).stripTrailingZeros();
                BigDecimal toNewBalance = toOldBalance.add(transaction.getAmount()).stripTrailingZeros();

                fromWallet.setBalance(fromNewBalance);
                toWallet.setBalance(toNewBalance);
            }
        }
    }

    private Wallet[] orderWalletsForLock(Wallet fromWallet, Wallet toWallet) {
        // ordering wallets by comparing their IDs to ensure that locks
        // in all concurrent transactions are taken in the same order
        // to avoid deadlocks
        if (fromWallet.getId().compareTo(toWallet.getId()) <= 0) {
            return new Wallet[] { fromWallet, toWallet };
        } else {
            return new Wallet[] { toWallet, fromWallet };
        }
    }
}
