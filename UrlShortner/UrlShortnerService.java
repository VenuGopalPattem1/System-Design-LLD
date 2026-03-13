package UrlShortner;

public interface UrlShortnerService {
    String shortenUrl(String longUrl);
    String shortenUrl(String longUrl,String aliasName);
    String getLongUrl(String shortUrl);
    int getCnt(String shortUrl);
}
