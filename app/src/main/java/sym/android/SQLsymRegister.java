package sym.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walker on 4/16/16.
 */
@Deprecated
public class SQLsymRegister extends AsyncTask<String, Integer, ResultSet> {

    protected Integer createOrUpdateRegistration(String emailId, String token, String sender_id){

         ResultSet rs = doInBackground("select * from tokensTable");
        List<TokensTable> tokTbl = null;
        try {
            int i=0;
            boolean found = false;
            TokensTable tt = new TokensTable();
            if(!rs.next()){
                ArrayList<Integer> resultList = doInBackgroundUpdate("insert into tokensTable (email,token,senderId) values (emailId,token,sender_id)");
                Log.d("Add"," resultList: "+resultList.get(0));
            }
            else{
                while(rs.next()) {
                   String rsEmailId = rs.getString("email");
                   String rsToken = rs.getString("token");

                   tt.setEmailId(rsEmailId);
                   tt.setSender_id(rs.getString("senderId"));
                   tt.setToken(rsToken);
                   tt.setId(rs.getInt("id"));
                    Log.d("Tt", "Email Id:" + rsEmailId+"Token: "+rsToken);
                    if(rsEmailId.equals(emailId) && !token.equals(rsToken)){
                    // Run update query for the token
                        Log.d("Tt", "Not same");
                        found = true;
                    }

                            tokTbl.add(i, tt);
                       }
                if(!found) {
                    ResultSet rsi = doInBackground("insert into tokensTable (email,token,senderId) values (emailId,token,sender_id)");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (0);
    }
    protected ResultSet doInBackground(String... querys) {

        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://104.197.1.115:3306/user_tokens", "root", "hariharan");
            Log.d("","Connection established");
            ResultSet resultSet = null;
            Statement statement = connection.createStatement();
            for (String query : querys) {
                 resultSet = statement.executeQuery(query);


            }
            return(resultSet);
        }catch (Exception e){
            Log.d("SQL","Error during SQL connection " + e.getMessage());
            return(null);
        }

    }
    protected ArrayList<Integer> doInBackgroundUpdate(String... querys) {

        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://104.197.1.115:3306/user_tokens", "root", "hariharan");
            ArrayList<Integer> result = new ArrayList<Integer>();
            Statement statement = connection.createStatement();
            for (String query : querys) {
                result.add( statement.executeUpdate(query));


            }
            return(result);
        }catch (Exception e){
           Log.d("SQL","Error during SQL connection "+e.getStackTrace());
            return(null);
        }

    }
}
