package org.slim3plus.tx;

import java.lang.annotation.*;

/**
 * トランザクションアノテーション
 * 
 * @author ryohei
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Tx {
	/**
	 *
	 * ConcurrentModificationExceptionが発生した場合のリトライ回数
	 *
	 * リトライ回数 0ならリトライしない。<br/>
	 * 負数なら無限にリトライ<br/>
	 * リトライするなら5回くらいがよい<br/>
	 * 
	 * @return
	 */
	int cmeRetry() default 0;

	/**
	 * タイムアウト時間<br/>
	 * 負数ならタイムアウト時間なし<br/>
	 * 
	 * @return タイムアウト時間
	 */
	long cmeTimeoutMillis() default -1;

	/**
	 * @return リトライ時のインターバル(ミリ秒)
	 */
	long cmeRetryIntervalMills() default 50;
}
