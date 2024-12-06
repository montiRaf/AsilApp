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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.Vaccinazione;
import it.uniba.dib.sms232436.activities.adapters.VaccinazioniAdapter;

public class ListaVaccinazioni extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VaccinazioniAdapter adapter;
    private List<Vaccinazione> vaccinazioni;
    private DatabaseReference vaccinazioniRef;
    private TextView noVaccinazioniText;
    private Paziente paziente;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vaccinazioni);

        recyclerView = findViewById(R.id.vaccinazioni_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vaccinazioni = new ArrayList<>();
        adapter = new VaccinazioniAdapter(this, vaccinazioni);
        recyclerView.setAdapter(adapter);

        noVaccinazioniText = findViewById(R.id.no_vaccinazioni_text);

        // Firebase database reference
        vaccinazioniRef = FirebaseDatabase.getInstance().getReference("Vaccinazioni");

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        String idPaziente = paziente.getId().toString();

        fetchVaccinazioni(idPaziente);
        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.list_vaccination));
    }

    private void fetchVaccinazioni(String idPaziente) {
        vaccinazioniRef.orderByChild("idPaziente").equalTo(idPaziente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vaccinazioni.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("FirebaseDebug", "DataSnapshot Key: " + dataSnapshot.getKey());
                    Log.d("FirebaseDebug", "DataSnapshot Value: " + dataSnapshot.getValue());
                    Vaccinazione vaccinazione = dataSnapshot.getValue(Vaccinazione.class);
                    Log.d("FirebaseDebug", "Vaccino: " + vaccinazione.getIdVaccinazione() + ", " + vaccinazione.getVaccino() + ", " + vaccinazione.getIdPaziente());
                    if (vaccinazione != null) {
                        vaccinazioni.add(vaccinazione);
                    }
                }

                if (vaccinazioni.isEmpty()) {
                    // Mostra la TextView e nasconde la RecyclerView se non ci sono vaccinazioni
                    noVaccinazioniText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    // Nasconde la TextView e mostra la RecyclerView se ci sono vaccinazioni
                    noVaccinazioniText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaVaccinazioni.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
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