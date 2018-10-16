package org.slim3plus.cache;

/**
 * Created by katsume on 2018/04/03.
 */
public class CacheNotFoundException extends Exception {


    public CacheNotFoundException(){
        super();
    }

    public CacheNotFoundException(Throwable cause) {
        super(cause);
    }
}
