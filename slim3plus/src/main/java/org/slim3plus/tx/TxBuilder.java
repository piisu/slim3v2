package org.slim3plus.tx;

import java.lang.annotation.Annotation;

public class TxBuilder {

    private TxImpl impl = new TxImpl();

    public TxBuilder retry(int cmeRetry) {
        impl._cmeRetry = cmeRetry;
        return this;
    }

    public TxBuilder retryTimeout(long cmeTimeoutMillis) {
        impl._cmeTimeoutMillis = cmeTimeoutMillis;
        return this;
    }

    public TxBuilder retryInterval(int cmeRetryIntervalMills) {
        impl._cmeRetryIntervalMills = cmeRetryIntervalMills;
        return this;
    }


    public Tx build() {
        return impl;
    }

    private static class TxImpl implements Tx {
        private int _cmeRetry = 0;
        private long _cmeTimeoutMillis =-1;
        private long _cmeRetryIntervalMills = 50;
        private boolean _xgtx = true;


        public int cmeRetry() {
            return _cmeRetry;
        }

        public long cmeTimeoutMillis() {
            return _cmeTimeoutMillis;
        }

        public long cmeRetryIntervalMills() {
            return _cmeRetryIntervalMills;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    }
}
