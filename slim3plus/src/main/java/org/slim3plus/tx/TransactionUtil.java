package org.slim3plus.tx;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class TransactionUtil {

    private static Logger logger
            = Logger.getLogger(TransactionUtil.class.getName());

    private static Transaction getCurrentTransaction() {
        final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return ds.getCurrentTransaction(null);
    }

    private static Transaction beginTransaction() {
        final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return ds.beginTransaction(TransactionOptions.Builder.withXG(true));
    }

    public static <T> T transaction(Callable<T> callable, Tx options) throws Exception {

        final long start = System.currentTimeMillis();

        final Tx txRetry = options;

        Transaction tx = getCurrentTransaction();

        if (tx != null && tx.isActive()) {
            return callable.call();
        }

        final int count = txRetry.cmeRetry();
        final long timeoutMills = txRetry.cmeTimeoutMillis();
        final long retryIntervalMills = txRetry.cmeRetryIntervalMills();

        if (count < 0 && timeoutMills < 0) {
            logger.warning("TxRetry count and timeout mills are infinite.");
        }

       int retryCount = 0;

        while (true) {
            tx = beginTransaction();
            try {
                final T obj = callable.call();
                tx.commit();
                return obj;
            } catch (ConcurrentModificationException cme) {
                if (tx.isActive()) {
                    tx.rollback();
                }

                if (count == retryCount) {
                    // リトライ回数オーバー
                    throw cme;
                }
                final long elapsed = System.currentTimeMillis() - start;

                if (0 < timeoutMills && timeoutMills <= elapsed) {
                    // タイムアウト
                    throw cme;
                }

                if (retryIntervalMills != 0) {
                    Thread.sleep(retryIntervalMills);
                }
                retryCount++;
            } catch (Exception ex) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw ex;
            }
        }
    }
}
