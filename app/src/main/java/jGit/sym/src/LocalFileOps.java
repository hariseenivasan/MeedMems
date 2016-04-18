package jGit.sym.src;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kiran on 4/17/16.
 */
public class LocalFileOps {
    private void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        String fileName = null;
        try {
            fileName = inputPath.substring(inputPath.lastIndexOf("/"));
            in = new FileInputStream(inputPath + fileName);
            out = new FileOutputStream(outputPath + fileName);

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
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void deleteFile(String inputPath) {
        try {
            // delete the original file
            new File(inputPath).delete();

        }

        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }
}
