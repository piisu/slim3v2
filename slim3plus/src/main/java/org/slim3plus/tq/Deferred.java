package org.slim3plus.tq;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Deferred {

    /**
     * キュー名
     *
     * @return
     */
    String queueName() default "";

    long randomCountdownMillis() default -1;

    long countdownMillis() default -1;

    long etaMillis() default -1;

    /**
     * AppEngineの管理画面のリクエストヘッダーで実行内容が確認できるようにするかどうか。
     * 運用向けのオプション
     * @return trueならリクエストヘッダに出力する
     */
    boolean headerOption() default false;

    /**
     * タスクキューの追加にトランザクションを使用するか？
     *
     * @return trueなら現在のトランザクションに参加する
     */
    boolean tx() default false;
}
