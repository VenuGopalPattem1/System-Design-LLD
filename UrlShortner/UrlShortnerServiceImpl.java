package UrlShortner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlShortnerServiceImpl implements UrlShortnerService{
    private Map<String,UrlMapping> shortToLong=new HashMap<>();
    private Map<String,String> longToShort=new HashMap<>();
    private Base62Ecncode encode;
    private AtomicInteger counter=new AtomicInteger(100_000);

    public UrlShortnerServiceImpl(){
        encode=new Base62Ecncode();
    }


    @Override
    public String shortenUrl(String longUrl) {
        if(longToShort.containsKey(longUrl)){
            return longToShort.get(longUrl);
        }
        String url=encode.encode(counter.getAndIncrement());
        shortToLong.put(url, new UrlMapping(url, longUrl));
        longToShort.put(longUrl, url);
        return url;
    }

    @Override
    public String shortenUrl(String longUrl, String aliasName) {
        if(shortToLong.containsKey(aliasName)){
            return "this "+aliasName+" alias name is already taken";
        }
        shortToLong.put(aliasName, new UrlMapping(aliasName, longUrl));
        longToShort.put(longUrl, aliasName);
        return aliasName;
    }

    @Override
    public String getLongUrl(String shortUrl) {
      if(shortToLong.containsKey(shortUrl)){
        UrlMapping mapping=shortToLong.get(shortUrl);
        mapping.incrementCnt();
        return mapping.getLongUrl();
      }
      throw new ShortUrlNotFound(shortUrl);
    }

    @Override
    public int getCnt(String shortUrl) {
        if(shortToLong.containsKey(shortUrl)){
        return shortToLong.get(shortUrl).getCnt().get();
      }
      throw new ShortUrlNotFound(shortUrl);
    }
    
}
