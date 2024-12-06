package it.uniba.dib.sms232436.classes;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetPostiVicini extends AsyncTask<Object, String, String> {

    private String googlemapPlaceData, url;

    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURLPosti downloadURLPosti = new DownloadURLPosti();
        try {
            googlemapPlaceData = downloadURLPosti.LeggiURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlemapPlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> postiList = null;
        DataParser dataParser = new DataParser();
        postiList = dataParser.parse(s);

        DisplayPosti(postiList);

    }

    private void DisplayPosti(List<HashMap<String, String>> postiList){
        for(int i = 0; i<postiList.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> posto = postiList.get(i);

            String nomePosto = posto.get("place_name");
            String vicinanza = posto.get("vicinity");
            double latitudine = Double.parseDouble(posto.get("lat"));
            double longitudine = Double.parseDouble(posto.get("lng"));
            String riferimento = posto.get("reference");

            LatLng latLng = new LatLng(latitudine, longitudine);
            markerOptions.position(latLng);
            markerOptions.title(nomePosto + " " + vicinanza);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);
        }
    }
}
