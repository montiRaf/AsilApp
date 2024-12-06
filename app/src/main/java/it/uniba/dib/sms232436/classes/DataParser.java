package it.uniba.dib.sms232436.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getPostoVicino(JSONObject googlePlacesJSON){

        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nomePosto = "-NA-";
        String vicinanza = "-NA-";
        String latitudine = "";
        String longitudine = "";
        String riferimento = "";

        try{
            if(!googlePlacesJSON.isNull("name")){
                nomePosto = googlePlacesJSON.getString("name");
            }
            if(!googlePlacesJSON.isNull("vicinity")){
                nomePosto = googlePlacesJSON.getString("vicinity");
            }

            latitudine = googlePlacesJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitudine = googlePlacesJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            riferimento = googlePlacesJSON.getString("reference");

            googlePlaceMap.put("place_name", nomePosto);
            googlePlaceMap.put("vicinity", vicinanza);
            googlePlaceMap.put("lat", latitudine);
            googlePlaceMap.put("lng", longitudine);
            googlePlaceMap.put("reference", riferimento);

        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getPostiVicini (JSONArray jsonArray) {

        int c = jsonArray.length();
        List<HashMap<String, String>> PostiVicini = new ArrayList<>();
        HashMap<String, String> PostiViciniMap = null;

        for(int i=0; i<c; i++){
            try{
                PostiViciniMap = getPostoVicino( (JSONObject) jsonArray.get(i));
                PostiVicini.add(PostiViciniMap);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }

        return PostiVicini;
    }

    public List<HashMap<String, String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return getPostiVicini(jsonArray);
    }
}
