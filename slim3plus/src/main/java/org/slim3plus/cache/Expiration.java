package org.slim3plus.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by katsume on 2018/04/03.
 */
public class Expiration implements Serializable {


    private long milliSeconds;

    private Expiration(long milliSeconds) {
        this.milliSeconds = milliSeconds;
    }

    /**
     * 時間を指定して有効期限を決める
     *
     * @param hoursDelay
     * @return
     */
    public static Expiration byDeltaHours(int hoursDelay) {
        return new Expiration((new Date().getTime()) + hoursDelay * 60 * 60
                * 1000);
    }

    /**
     * 分を指定して有効期限を決める
     *
     * @param minutesDelay
     * @return
     */
    public static Expiration byDeltaMinutes(int minutesDelay) {
        return new Expiration((new Date().getTime()) + minutesDelay * 60 * 1000);
    }

    /***
     * 秒を指定して有効期限を決める
     *
     * @param secondsDelay
     * @return
     */
    public static Expiration byDeltaSeconds(int secondsDelay) {
        return new Expiration((new Date().getTime()) + secondsDelay * 1000);
    }

    public static Expiration onDate(Date expiratinTime) {
        return new Expiration((expiratinTime.getTime()));
    }

    public int getSecondsValue() {
        return (int) (milliSeconds / 1000);
    }

    public long getMilliSeconds() {
        return milliSeconds;
    }


    public boolean isExpired() {
        return isExpired(new Date());
    }

    public boolean isExpired(final Date now) {
        return milliSeconds < (now.getTime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (milliSeconds ^ (milliSeconds >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Expiration other = (Expiration) obj;
        if (milliSeconds != other.milliSeconds)
            return false;
        return true;
    }

}