package com.example.aaldridge.myapplication;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by aaldridge on 09/06/2016.
 */
public class Commands {

    public static String PÁGINA_SIGUIENTE = "siguiente";
    public static String PÁGINA_ANTERIOR = "anterior";

    public static void performClick(Integer xPosition, Integer yPosition) {

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String cmd = "/system/bin/input tap " + xPosition + " " + yPosition + "\n";
            os.writeBytes(cmd);
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
