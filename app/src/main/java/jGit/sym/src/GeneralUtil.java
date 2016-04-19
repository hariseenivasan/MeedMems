package jGit.sym.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.eclipse.jgit.api.errors.GitAPIException;

import javax.net.ssl.HttpsURLConnection;

import sym.android.MetaData;

/**
 * Created by kiran on 4/18/16.
 */
public class GeneralUtil {
    // HTTP POST request
    public static String sendPost(String data) throws Exception {

        Log.d("GeneralUtil","In GeneralUtil.sendPost Method");

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

    public static Map<String,String> localUpdateTokenMap(Context context) {

        Log.d("GeneralUtil","In GeneralUtil.localUpdateTokenMap Method");
        String repoName="SYM";
        JGitOps gitUtil = new JGitOps(context);
        File f = new File(context.getFilesDir().getPath()+"/"+repoName);
        Log.d("localUpdateTokenMap","the path is "+f.getAbsolutePath());
        if(f.isDirectory()) {
            try {
//                gitUtil.setTracker(repoName);
                gitUtil.pull(repoName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }else {
            try {
                gitUtil.clone(repoName);
//                gitUtil.setTracker(repoName);
            } catch (GitAPIException e) {
                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

        return readFile(f.getAbsolutePath()+"/tokensmetadata");

    }

    public static void remoteUpdateTokenMap(Context context, String emailid, String token) {

        Log.d("GeneralUtil","In GeneralUtil.remoteUpdateTokenMap Method");
        String repoName="SYM";
        JGitOps gitUtil = new JGitOps(context);
        File f = new File(context.getFilesDir().getPath()+"/"+repoName);
        writeFile(f.getAbsolutePath() + "/tokensmetadata", emailid, token);

        try {
            gitUtil.setTracker(repoName);
            gitUtil.add(repoName);
            gitUtil.commit(repoName, "committing new entry for "+emailid);
            gitUtil.push(repoName);
        } catch (IOException e) {
            Log.d("GeneralUtil",e.getMessage());
            e.printStackTrace();
        } catch (GitAPIException e) {
            Log.d("GeneralUtil",e.getMessage());
            e.printStackTrace();
        }
    }

    private static Map<String,String> readFile(String filePath) {
        Log.d("GeneralUtil", "In GeneralUtil.readFile Method");

        Map<String,String> testMap = new HashMap<String, String>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim() != "") {
                    testMap.put(line.split("=")[0], line.split("=")[1]);
                }
            }
            in.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return testMap;
    }

    private static void writeFile(String filePath, String emailid, String token) {
        Log.d("GeneralUtil","In GeneralUtil.writeFile Method");
        try
        {
            FileWriter fw = new FileWriter(filePath,true); //the true will append the new data
            fw.write(emailid+"="+token+"\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static boolean pushPhotos(MetaData metaData) {
        boolean retVal = false;
        String localPath = getGalleryPath();
        Log.d("GeneralUtil","pushPhotos: the DCIM path is "+localPath);

        JGitOps gitOps = new JGitOps(localPath);

        //Create a remote repository and clone it to local
        try {
            gitOps.createRemoteRepo(metaData.getGroupName());
            gitOps.clone(metaData.getGroupName());
        } catch (IOException e) {
            Log.d("GeneralUtil:pushPhotos",e.getMessage());
        } catch (GitAPIException e) {
            Log.d("GeneralUtil:pushPhotos", e.getMessage());
        }

        //copy the files from gallery to local repo location
        for (String path:metaData.getFileNameList()) {
            Log.d("GeneralUtil","pushPhotos: copying file "+path+" to "+localPath+"/"+metaData.getGroupName());
            copyFile(path,localPath+"/"+metaData.getGroupName());
        }

        //git add,commit,push to remote
        try {
           // gitOps.setTracker(metaData.getGroupName());
            gitOps.add(metaData.getGroupName());
            gitOps.commit(metaData.getGroupName(), "adding " + metaData.getSizeList() + " number of photos");
            gitOps.push(metaData.getGroupName());
            return true;
        } catch (IOException e) {
            Log.d("GeneralUtil:pushPhotos", e.getMessage());
        } catch (GitAPIException e) {
            Log.d("GeneralUtil:pushPhotos", e.getMessage());
        }

        return retVal;
    }
    public static boolean pullPhotos(MetaData metaData) {
        boolean retVal = false;
        String localPath = getGalleryPath();
        Log.d("GeneralUtil","pullPhotos: the DCIM path is "+localPath);

        JGitOps gitOps = new JGitOps(localPath);
        File f = new File(localPath+"/"+metaData.getGroupName());

        // clone or pull a local repository
        try {

            if(f.isDirectory()) {
                gitOps.pull(metaData.getGroupName());
                return true;
            } else {
                gitOps.clone(metaData.getGroupName());
                gitOps.pull(metaData.getGroupName());
                return true;
            }

        } catch (IOException e) {
            Log.e("GeneralUtil:pullPhotos",e.getMessage());
        } catch (GitAPIException e) {
            Log.e("GeneralUtil:pullPhotos", e.getMessage());
        }

        return retVal;
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM;
    }

    private static void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        String fileName = null;
        try {
            fileName = inputPath.substring(inputPath.lastIndexOf("/")+1);
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("GeneralUtil:copyFile", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("GeneralUtil:copyFile", e.getMessage());
        }

    }
}
