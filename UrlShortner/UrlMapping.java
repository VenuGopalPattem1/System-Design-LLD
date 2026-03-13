package UrlShortner;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlMapping {
    private String shortUrl;
    private String longUrl;
    private LocalDateTime createAt;
    private LocalDateTime expiredAt;
    private AtomicInteger cnt=new AtomicInteger(0);

    public UrlMapping(String shortUrl, String longUrl){
        this.shortUrl=shortUrl;
        this.longUrl=longUrl;
    }

    public boolean isExpired(){
        return expiredAt!=null&&LocalDateTime.now().isAfter(expiredAt);
    }

    public void incrementCnt(){
        cnt.incrementAndGet();
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public AtomicInteger getCnt() {
        return cnt;
    }
    
    
}