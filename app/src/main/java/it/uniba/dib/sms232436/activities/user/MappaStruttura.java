package it.uniba.dib.sms232436.activities.user;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.PointOfInterest;


public class MappaStruttura extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference mDatabase;
    private GoogleMap myMap;

    private List<PointOfInterest> POIs;

    private double latitudine, longitudine;
    private Context context;
    private Paziente paziente;
    private String nomeS, codiceStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null) {
            latitudine = intent.getDoubleExtra("lat", 0);
            longitudine = intent.getDoubleExtra("lng", 0);
            nomeS = intent.getStringExtra("nomeStruttura");
            codiceStr = intent.getStringExtra("codiceStruttura");
        }
        setContentView(R.layout.activity_mappa_struttura);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);

        // Recupera l'Intent e i dati
        Intent intent_paziente = getIntent();
        if (intent_paziente != null && intent_paziente.getExtras() != null) {
            paziente = (Paziente) intent_paziente.getSerializableExtra("paziente");
            Log.d("Nome Paziente", "" + paziente.getNome());
        }
        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.map));

    }

    public void loadPOIs(String codeStr) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Strutture_Vicine");
        POIs = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nomeStr, lat , lng , cat , codiceStru;
                    codiceStru = snapshot.child("codiceStrutturaVicina").getValue(String.class);
                    nomeStr = snapshot.child("nomeStruttura").getValue(String.class);
                    lat = snapshot.child("latitudine").getValue(String.class);
                    lng = snapshot.child("longitudine").getValue(String.class);
                    cat = snapshot.child("categoria").getValue(String.class);
                    Log.d("FirebaseData", "Codice Luogo: " + codiceStru + " " + nomeStr + " " + lat + " " + lng + " " + cat);
                    if(Objects.equals(codiceStru, codeStr)){
                        PointOfInterest poi = new PointOfInterest(codiceStru, nomeStr, lat, lng, cat);
                        POIs.add(poi);
                    }

                }
                markPOIs();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("FirebaseError", "Failed to read value.", error.toException());
            }
        });
    }


    public void markPOIs(){
        if(POIs.isEmpty()){
            Log.d("FirebaseData", "Nessun POI trovato");
        }
        for(PointOfInterest poi : POIs){
            Log.d("FirebaseData", "Codice Luogo: " + poi.getCodiceStrutturaVicina() + " " + poi.getNomeStruttura() + " " + poi.getLatitudine() + " " + poi.getLongitudine() + " " + poi.getCategoria());
            double latit, longit;
            latit = Double.parseDouble(poi.getLatitudine());
            longit = Double.parseDouble(poi.getLongitudine());
            Log.d("FirebaseData", "Latitudine: " + latit + " Longitudine: " + longit);
            LatLng point = new LatLng(latit, longit);
            switch(poi.getCategoria()){
                case "Supermarket":
                    myMap.addMarker(new MarkerOptions().position(point).title(poi.getNomeStruttura()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    break;
                case "Ospedale":
                    myMap.addMarker(new MarkerOptions().position(point).title(poi.getNomeStruttura()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    break;
                case "Farmacia":
                    myMap.addMarker(new MarkerOptions().position(point).title(poi.getNomeStruttura()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                default:
                    break;
            }


        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng structure = new LatLng(latitudine, longitudine);
        myMap.addMarker(new MarkerOptions().position(structure).title(nomeS).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(structure, 15));
        loadPOIs(codiceStr);

    }

    // Gestione della bottom bar --> da copiare in tutte le altre activity
    private void initializeBottomBar(){
        int unclickable = 0;
        ImageButton btnHome = findViewById(R.id.home_btn);
        ImageButton btnSettings = findViewById(R.id.settings_btn);
        ImageButton btnSos = findViewById(R.id.sos_btn);
        ImageButton btnMessage = findViewById(R.id.message_btn);
        ImageButton btnHP = findViewById(R.id.hp_btn);

        context = this;

        unclickable = changeIconsColor();
        if(unclickable != 1){
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImpostazioniPaziente.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 2){
            btnSos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Sos.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 3){
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomePaziente.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 4){
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListaMessaggi.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 5){
            btnHP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Misurazioni.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

    }

    private int changeIconsColor(){
        int value = 0;
        ImageView icon;
        String classname = this.getClass().getSimpleName();
        Log.d("CurrentActivity", "La classe corrente è: " + classname);
        switch(classname){
            case "ImpostazioniPaziente":
                value = 1;
                break;
            case "Sos":
                value = 2;
                break;
            case "HomePaziente":
                value = 3;
                break;
            case "ListaMessaggi":
                value = 4;
                break;
            case "Misurazioni":
                value = 5;
                break;
        }

        return value;
    }

}