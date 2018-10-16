package org.slim3plus.tx;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * 
 * トランザクションを管理するインターセプタ ConcurrentModificationExceptionが発生した場合にのみ、リトライを行う
 * 
 * @author ryohei
 * 
 */
public class TxInterceptor implements MethodInterceptor {

	private static final Logger logger = Logger.getLogger(TxInterceptor.class
			.getName());

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		return TransactionUtil.transaction(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				try {
					return invocation.proceed();
				}
				catch(Exception ex) {
					throw ex;
				}
				catch(Throwable th) {
					throw new Exception(th);
				}
			}
		}, invocation.getMethod().getAnnotation(Tx.class));
	}
}
