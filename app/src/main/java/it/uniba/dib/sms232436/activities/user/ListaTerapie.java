package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.TextView;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.Terapia;
import it.uniba.dib.sms232436.activities.adapters.TerapiaAdapter;

public class ListaTerapie extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TerapiaAdapter adapter;
    private List<Terapia> terapie;
    private DatabaseReference terapieRef;
    private Paziente paziente;
    private TextView noTerapieText;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_terapie);

        recyclerView = findViewById(R.id.vaccinazioni_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noTerapieText = findViewById(R.id.no_terapie_text);
        terapie = new ArrayList<>();
        adapter = new TerapiaAdapter(this, terapie);
        recyclerView.setAdapter(adapter);

        // Firebase database reference
        terapieRef = FirebaseDatabase.getInstance().getReference("Terapie");

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        String idPaziente = paziente.getId().toString();
        Log.d("IdPaziente to String", paziente.getId().toString());

        fetchTerapie(idPaziente);
        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.list_therapies));
    }

    private void fetchTerapie(String idPaziente) {
        terapieRef.orderByChild("idPaziente").equalTo(idPaziente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                terapie.clear();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date currentDate = new Date();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Terapia terapia = dataSnapshot.getValue(Terapia.class);
                    Log.d("FirebaseDebug", "Terapie: " + terapia.getNomeFarmaco() + ", " + terapia.getIdPaziente() + ", " + terapia.getDataInizio());
                    if (terapia != null) {
                        try {
                            Date dataFine = dateFormat.parse(terapia.getDataFine());
                            if (dataFine != null && dataFine.before(currentDate)) {
                                // Elimina la terapia dal database se la data di fine è superata
                                dataSnapshot.getRef().removeValue();
                            } else {
                                // Aggiungi alla lista solo le terapie non scadute
                                terapie.add(terapia);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (terapie.isEmpty()) {
                    // Mostra la TextView e nasconde la RecyclerView se non ci sono terapie
                    noTerapieText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    // Nasconde la TextView e mostra la RecyclerView se ci sono terapie
                    noTerapieText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaTerapie.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
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
