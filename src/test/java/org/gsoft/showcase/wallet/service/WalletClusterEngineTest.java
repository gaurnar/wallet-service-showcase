package org.gsoft.showcase.wallet.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.domain.WalletFactory;
import org.junit.Before;
import org.junit.Test;

public class WalletClusterEngineTest {

    private WalletClusterEngine engine;

    private WalletStorage mockStorage;

    @Before
    public void setUp() {
        mockStorage = mock(WalletStorage.class);
        engine = new WalletClusterEngine(mockStorage);
    }

    @Test
    public void should_not_remember_withdrawal_as_completed_on_failure() {
        UUID transactionId = UUID.randomUUID();
        Wallet wallet = WalletFactory.createWallet(UUID.randomUUID(), BigDecimal.valueOf(10));

        Exception mockStorageException = new RuntimeException("Ooops!");

        doThrow(mockStorageException)
            .when(mockStorage).get(wallet.getId());

        Exception exception = null;

        try {
            engine.withdraw(wallet.getId(), transactionId, BigDecimal.valueOf(5));
        } catch (Exception e) {
            exception = e;
        }

        assertEquals(mockStorageException, exception);

        doReturn(wallet)
            .when(mockStorage).get(wallet.getId());

        engine.withdraw(wallet.getId(), transactionId, BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(5), wallet.getBalance());
    }

    @Test
    public void should_not_remember_addition_as_completed_on_failure() {
        // TODO remove copy paste with above

        UUID transactionId = UUID.randomUUID();
        Wallet wallet = WalletFactory.createWallet(UUID.randomUUID(), BigDecimal.valueOf(0));

        Exception mockStorageException = new RuntimeException("Ooops!");

        doThrow(mockStorageException)
            .when(mockStorage).get(wallet.getId());

        Exception exception = null;

        try {
            engine.add(wallet.getId(), transactionId, BigDecimal.valueOf(5));
        } catch (Exception e) {
            exception = e;
        }

        assertEquals(mockStorageException, exception);

        doReturn(wallet)
            .when(mockStorage).get(wallet.getId());

        engine.add(wallet.getId(), transactionId, BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(5), wallet.getBalance());
    }
}