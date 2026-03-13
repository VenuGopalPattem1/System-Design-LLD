package UrlShortner;

public class Main {
    public static void main(String[] args) {
        UrlShortnerService service=new UrlShortnerServiceImpl();
        String s=service.shortenUrl("https://claude.ai/chat/a421623f-3eec-4234-9e0b-0f9a68956a99");
        System.out.println(s);
        System.out.println("------------------------------------");
        String longUrl=service.getLongUrl("aaaaAa4");
        System.out.println(longUrl);
        System.out.println("------------------------------------");
        String longUrl1=service.getLongUrl("aaaaAa4");
        System.out.println(longUrl1);
        System.out.println("------------------------------------");
        String longUrl2=service.getLongUrl("aaaaAa4");
        System.out.println(longUrl2);
        System.out.println("------------------------------------");
        System.out.println(service.getCnt("aaaaAa4"));
        String ss=service.shortenUrl("https://claude.ai/chat/a421623f-4234-9e0b-0f9a68956a99");
        System.out.println(ss);
        System.out.println("------------------------------------");
        String longUrls=service.getLongUrl("aaaaAa5");
        System.out.println(longUrls);
        System.out.println("------------------------------------");
        String longUrl1s=service.getLongUrl("aaaaAa5");
        System.out.println(longUrl1s);
        System.out.println("------------------------------------");
        String longUrl2s=service.getLongUrl("aaaaAa5");
        System.out.println(longUrl2s);
        System.out.println("------------------------------------");
        System.out.println(service.getCnt("aaaaAa5"));
    }
}
