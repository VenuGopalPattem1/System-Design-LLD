package UrlShortner;

public class ShortUrlNotFound extends RuntimeException{
    public ShortUrlNotFound(String code){
        super("Short URL not found: " + code);
    }
}
