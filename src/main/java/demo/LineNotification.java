package demo;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class LineNotification {

    private String apiEndpoint ;

    private String secretKey ;

    private String message;

    public LineNotification(String apiEndpoint, String secretKey, String message) {
        this.apiEndpoint = apiEndpoint;
        this.secretKey = secretKey;
        this.message = message;
    }

    public static LineBuilder getBuilder(){
        return new LineBuilder();
    }

    public enum LineStatus{SUCCESS , FAIL}

    public LineStatus submit(){
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add("Authorization", getAuthorization());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( this.apiEndpoint, request , String.class );

        }
        catch(Exception e){
            return LineStatus.FAIL;
        }

        return LineStatus.SUCCESS;
    }

    private String getAuthorization() {
        return String.format("Bearer %s" , this.secretKey);
    }

    public static void main(String[] args){
        LineStatus result = LineNotification.getBuilder()
                .withEndpoint("https://notify-api.line.me/api/notify")
                .withSecretKey("abcdefghijklmnopqrstuvwxyz")
                .withMessage("Test send from code")
                .withMessage("Line 2")
                .build()
                .submit();

        System.out.println(result);
    }

    public static class LineBuilder{
        private String apiEndpoint ;
        private String secretKey;
        private List<String> messageL;

        public LineBuilder(){
            messageL = new ArrayList<String>();
        }

        public LineBuilder withEndpoint(String endpoint){
            this.apiEndpoint = endpoint;
            return this;
        }
        public LineBuilder withMessage(String message){
            messageL.add(message);
            return this;
        }

        public LineBuilder withSecretKey(String secretKey){
            this.secretKey = secretKey;
            return this;
        }

        public LineNotification build(){
            return new LineNotification(apiEndpoint , secretKey , StringUtils.arrayToDelimitedString(messageL.toArray(new String[messageL.size()]) , "\n" ) );
        }
    }
}
