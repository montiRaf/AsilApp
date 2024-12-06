package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.activities.adapters.ListaAllergieAdapter;
import it.uniba.dib.sms232436.classes.Paziente;

public class ListaAllergie extends AppCompatActivity {

    private RecyclerView recyclerViewAllergie;
    private ListaAllergieAdapter allergieAdapter;
    private List<String> listaAllergie;
    private TextView noAllergieText;
    private DatabaseReference databaseReferencePersona;
    private DatabaseReference databaseReferenceAllergie;
    private Context context;
    private Paziente paziente;
    private String idPaziente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_allergie);

        // Inizializzazione delle view
        recyclerViewAllergie = findViewById(R.id.recyclerViewAllergie);
        noAllergieText = findViewById(R.id.textViewNoAllergie);

        // Inizializzazione di Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReferencePersona = database.getReference("Pazienti");
        databaseReferenceAllergie = database.getReference("Allergie");

        // Inizializzazione della lista delle allergie
        listaAllergie = new ArrayList<>();
        allergieAdapter = new ListaAllergieAdapter(listaAllergie);
        recyclerViewAllergie.setAdapter(allergieAdapter);
        recyclerViewAllergie.setLayoutManager(new LinearLayoutManager(this));

        // Recupera lo username passato dall'Activity precedente (es. LoginActivity)
        // String username = getIntent().getStringExtra("username");
        // Recupera i dati del paziente dall'Intent
        paziente = (Paziente) getIntent().getSerializableExtra("paziente");
        idPaziente = paziente.getId();

        // Recupero ID paziente usando lo username
        recuperaIdPaziente(idPaziente);

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.lista_allergie));
    }

    private void recuperaIdPaziente(String username) {
        databaseReferencePersona.orderByChild("id").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot personaSnapshot : dataSnapshot.getChildren()) {
                        String idPaziente = personaSnapshot.child("id").getValue(String.class);
                        if (idPaziente != null) {
                            caricaAllergie(idPaziente);
                        } else {
                            Toast.makeText(ListaAllergie.this, getString(R.string.paziente_non_trovato), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ListaAllergie.this, getString(R.string.paziente_non_trovato), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListaAllergie.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void caricaAllergie(String idPaziente) {
        databaseReferenceAllergie.child(idPaziente).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAllergie.clear();
                for (DataSnapshot allergiaSnapshot : dataSnapshot.getChildren()) {
                    String allergia = allergiaSnapshot.getValue(String.class);
                    if (allergia != null) {
                        listaAllergie.add(allergia);
                    }
                }

                if (listaAllergie.isEmpty()) {
                    // Mostra la TextView e nasconde la RecyclerView se non ci sono terapie
                    noAllergieText.setVisibility(View.VISIBLE);
                    recyclerViewAllergie.setVisibility(View.GONE);
                }else {
                    // Nasconde la TextView e mostra la RecyclerView se ci sono terapie
                    noAllergieText.setVisibility(View.GONE);
                    recyclerViewAllergie.setVisibility(View.VISIBLE);
                }

                allergieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListaAllergie.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
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
