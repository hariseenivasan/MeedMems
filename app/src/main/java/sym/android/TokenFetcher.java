package sym.android;

import android.app.Application;

import java.util.List;
import java.util.Map;

/**
 * Created by kiran on 4/18/16.
 */
public class TokenFetcher extends Application {

    Map<String,String> tokenMap;

    public Map<String, String> getTokenMap() {
        return tokenMap;
    }

    public void setTokenMap(Map<String, String> tokenMap) {
        this.tokenMap = tokenMap;
    }
}
