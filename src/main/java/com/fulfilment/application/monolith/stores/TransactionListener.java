package com.fulfilment.application.monolith.stores;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import org.jboss.logging.Logger;

enum Operation {
    UPDATE, PATCH, CREATE
}

public class TransactionListener implements Synchronization {

    private static final Logger LOGGER = Logger.getLogger(TransactionListener.class.getName());

    private final Runnable afterTransactionCallback;
    private final Operation operation;
    private final Long storeId;

    public TransactionListener(Operation operation, Long storeId, Runnable afterTransactionCallback) {
        this.afterTransactionCallback = afterTransactionCallback;
        this.operation = operation;
        this.storeId = storeId;
    }

    @Override
    public void beforeCompletion() {
        // No-op
    }

    @Override
    public void afterCompletion(int status) {
        if (status == Status.STATUS_COMMITTED) {
            afterTransactionCallback.run();
        } else {
            LOGGER.warn("Transaction commit failed for " + operation + " for " + storeId + " with status " + status);
        }
    }
}
