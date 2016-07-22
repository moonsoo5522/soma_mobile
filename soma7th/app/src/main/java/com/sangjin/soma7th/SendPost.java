package com.sangjin.soma7th;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SendPost extends AsyncTask<Void, Void, String>
{
    public String SERVER_URL = "http://52.40.227.27:8080/soma7th/";
    private String packet = "";
    String responseMsg;
    URL url = null;

    public SendPost(String servlet) {
        SERVER_URL += servlet;
    }
    public SendPost() {
        SERVER_URL += "MainServlet";
    }

    public void addHeader(String header) throws UnsupportedEncodingException {
        packet="header="+header;
    }
    public void addThePkt(String paramString1, String paramString2) throws UnsupportedEncodingException {
        packet+=("&"+paramString1+"="+paramString2);
    }
    public void addThePkt(String paramString1, int paramString2) throws UnsupportedEncodingException {
        packet+=("&"+paramString1+"="+paramString2);
    }
    public void addThePkt(String paramString1, double paramString2) throws UnsupportedEncodingException {
        packet+=("&"+paramString1+"="+paramString2);
    }
    public String getPacket() {
        return packet;
    }

    @Override
    protected String doInBackground(Void[] paramArrayOfVoid)
    {
        return executeClient();
    }

    public String executeClient() {

        try {
            url = new URL(SERVER_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.setUseCaches (false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Length", "" +
                    Integer.toString(packet.getBytes("UTF-8").length));
            conn.setRequestProperty("Content-Language", "UTF-8");
            conn.setRequestMethod("POST");

            //Send request
            OutputStream osw = conn.getOutputStream();
            osw.write(packet.getBytes("UTF-8"));
            osw.flush();
            osw.close();

            packet = null;

            String line = "";
            InputStreamReader stream_reader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(stream_reader);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
            // Response from server after login process will be stored in response variable.
            responseMsg = sb.toString();
            // You can perform UI operations here

        } catch (MalformedURLException | ProtocolException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseMsg;
    }

    @Override
    protected void onPostExecute(String paramString) {

    }
}