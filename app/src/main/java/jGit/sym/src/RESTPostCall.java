package jGit.sym.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import android.util.Log;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kiran on 4/18/16.
 */
public class RESTPostCall {
    // HTTP POST request
    public static String sendPost(String data) throws Exception {

        String url = "https://gcm-http.googleapis.com/gcm/send";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "key=AIzaSyCf_1ropZcd-C5aqXNxZqW1H2WmHwDVaoA");

//        String data = "{ \"data\": {\n" +
//                "    \"score\": \"5x1\",\n" +
//                "    \"time\": \"15:10\"\n" +
//                "  },\n" +
//                "  \"to\" : \"efmgSA7UmlE:APA91bEaFNbzaX7MBDsq0P68gPmFyYjQLJLwvzzyuLPmDmnD5qY-kRCQ3Agh1mZDTHN5wFM1_vd7uBU5XZjWo2U1GUcmNIDEHqBULsuJb9s7AujjZLQH3Y5GKx4diEoU2AkPexX4R6Pz\"\n" +
//                "}";
        // Send post request

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        Log.d("POST call URL-", url);
        Log.d("Response Code : ", Integer.toString(responseCode));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Log.d("POST Call Response -",response.toString());
        return response.toString();

    }

}
