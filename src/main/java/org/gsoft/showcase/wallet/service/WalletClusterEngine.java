package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.domain.WalletFactory;
import org.gsoft.showcase.wallet.error.InvalidTransaction;
import org.gsoft.showcase.wallet.error.InvalidWalletCreationException;
import org.gsoft.showcase.wallet.error.WalletNotFoundException;

/**
 * TODO polish docs
 *
 * This engine is intended to be run per node in cluster mode.
 */
public class WalletClusterEngine implements WalletsFacade {
    private final WalletStorage storage;

    private final Map<UUID, ProcessedWithdrawal> processedWithdrawalsMap;
    private final Map<UUID, ProcessedAddition> processedAdditionsMap;

    public WalletClusterEngine(WalletStorage storage) {
        this.storage = storage;

        processedWithdrawalsMap = new HashMap<>();
        processedAdditionsMap = new HashMap<>();
    }

    @Override
    public void withdraw(UUID walletId, UUID transactionId, BigDecimal amount) {
        // TODO rework, not scalable
        synchronized (processedWithdrawalsMap) {
            ProcessedWithdrawal withdrawal = processedWithdrawalsMap.get(transactionId);
            if (withdrawal != null) {
                if (!withdrawal.getFromWallet().equals(walletId) || !withdrawal.getAmount().equals(amount)) {
                    throw new InvalidTransaction("different withdrawal parameters for known transaction");
                }
                return;
            }

            getWallet(walletId).withdraw(amount);

            processedWithdrawalsMap.put(transactionId, new ProcessedWithdrawal(walletId, amount));
        }
    }

    @Override
    public void add(UUID walletId, UUID transactionId, BigDecimal amount) {
        synchronized (processedAdditionsMap) {
            ProcessedAddition addition = processedAdditionsMap.get(transactionId);
            if (addition != null) {
                if (!addition.getToWallet().equals(walletId) || !addition.getAmount().equals(amount)) {
                    throw new InvalidTransaction("different addition parameters for known transaction");
                }
                return;
            }

            getWallet(walletId).add(amount);

            processedAdditionsMap.put(transactionId, new ProcessedAddition(walletId, amount));
        }
    }

    @Override
    public void createWallet(UUID id, BigDecimal initialBalance) {
        // TODO rework, not scalable
        synchronized (storage) {
            Wallet existingWallet = storage.get(id);
            if (existingWallet != null) {
                if (!existingWallet.getInitialBalance().equals(initialBalance)) {
                    throw new InvalidWalletCreationException("initial balance differs from initial balance for known "
                                                                 + "wallet");
                }
                return;
            }

            storage.put(WalletFactory.createWallet(id, initialBalance));
        }
    }

    @Override
    public void removeWallet(UUID id) {
        storage.remove(id);
    }

    @Override
    public BigDecimal getBalance(UUID walletId) {
        return Optional.ofNullable(storage.get(walletId))
            .map(Wallet::getBalance)
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    private Wallet getWallet(UUID walletId) {
        return Optional.ofNullable(storage.get(walletId))
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    private static class ProcessedWithdrawal {

        private final UUID fromWallet;
        private final BigDecimal amount;

        ProcessedWithdrawal(UUID fromWallet, BigDecimal amount) {
            this.fromWallet = fromWallet;
            this.amount = amount;
        }

        UUID getFromWallet() {
            return fromWallet;
        }

        BigDecimal getAmount() {
            return amount;
        }
    }

    private static class ProcessedAddition {

        private final UUID toWallet;
        private final BigDecimal amount;

        ProcessedAddition(UUID toWallet, BigDecimal amount) {
            this.toWallet = toWallet;
            this.amount = amount;
        }

        UUID getToWallet() {
            return toWallet;
        }

        BigDecimal getAmount() {
            return amount;
        }
    }
}
