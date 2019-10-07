package org.gsoft.showcase.wallet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.SimpleWallet;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.dto.TransactionRequest;
import org.junit.Before;
import org.junit.Test;

public class TransactionWalletSynchronizedProcessorTest {

    private TransactionWalletSynchronizedProcessor processor;

    private WalletRegistry mockRegistry;

    @Before
    public void setUp() {
        mockRegistry = mock(WalletRegistry.class);
        processor = new TransactionWalletSynchronizedProcessor(mockRegistry);
    }

    @Test
    public void should_revert_from_wallet_balance_change_on_to_wallet_failure() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        SimpleWallet aWallet = new SimpleWallet(aWalletId, BigDecimal.valueOf(10));

        RuntimeException oopsException = new RuntimeException("Oops!");

        Wallet bWallet = mock(Wallet.class);
        when(bWallet.getId()).thenReturn(bWalletId);
        when(bWallet.getBalance()).thenReturn(BigDecimal.valueOf(0));
        when(bWallet.getInitialBalance()).thenReturn(BigDecimal.valueOf(0));
        doThrow(oopsException).when(bWallet).setBalance(any());

        when(mockRegistry.get(aWalletId)).thenReturn(aWallet);
        when(mockRegistry.get(bWalletId)).thenReturn(bWallet);

        try {
            processor.process(new TransactionRequest(transactionId, aWalletId, bWalletId, "5"));
            fail();
        } catch (Exception e) {
            assertEquals(oopsException, e);
        }

        assertEquals(BigDecimal.valueOf(10), aWallet.getBalance());
    }
}