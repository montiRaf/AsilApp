package it.uniba.dib.sms232436.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadURLPosti {

    public String LeggiURL (String locURL) throws IOException {

        String Data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(locURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            Data = stringBuffer.toString();
            bufferedReader.close();
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }

        return Data;
    }
}
