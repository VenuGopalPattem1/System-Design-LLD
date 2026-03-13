package UrlShortner;

public class Base62Ecncode {
    private static final String CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public String encode(long l){
        StringBuilder sb=new StringBuilder();
        while(l>0){
            sb.append(CHARS.charAt((int)l%62));
            l=l/62;
        }
        // pad to 7 chars
        while(sb.length()<7){
            sb.append('a');
        }
        return sb.reverse().toString();
    }
}
