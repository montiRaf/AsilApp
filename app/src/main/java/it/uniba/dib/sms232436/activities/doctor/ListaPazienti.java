package it.uniba.dib.sms232436.activities.doctor;

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
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.activities.adapters.PazienteAdapter;

public class ListaPazienti extends AppCompatActivity implements PazienteAdapter.OnPazienteClickListener{

    private RecyclerView recyclerView;
    private PazienteAdapter pazienteAdapter;
    private List<Paziente> pazienteList;
    private DatabaseReference pazientiRef;
    private String medicoUsername;
    private Medico medico;
    private TextView emptyView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pazienti);

        recyclerView = findViewById(R.id.recyclerViewPazienti);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pazienteList = new ArrayList<>();
        pazienteAdapter = new PazienteAdapter(pazienteList, this);
        recyclerView.setAdapter(pazienteAdapter);
        emptyView = findViewById(R.id.emptyView);

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");

        // Recupera lo username dall'oggetto Medico
        if (medico != null) {
            medicoUsername = medico.getUsername();
            Log.d("USERNAME MEDICO", "" + medicoUsername);

        } else {
            Toast.makeText(this, "Errore nel recupero del medico", Toast.LENGTH_SHORT).show();
        }

        // Riferimento al database Firebase
        pazientiRef = FirebaseDatabase.getInstance().getReference("Pazienti");

        // Recupera i pazienti che hanno come medico_curante lo username del medico loggato
        fetchPazienti();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.patient_list));

        initializeBottomBar();
    }

    private void fetchPazienti() {
        pazientiRef.orderByChild("medico_curante").equalTo(medicoUsername)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pazienteList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Paziente paziente = snapshot.getValue(Paziente.class);
                            pazienteList.add(paziente);
                        }

                        // Controlla se la lista è vuota e gestisce la visibilità della TextView
                        if (pazienteList.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        pazienteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Gestione degli errori
                        Toast.makeText(ListaPazienti.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPazienteClick(int position) {
        // Ottieni il paziente cliccato
        Paziente paziente = pazienteList.get(position);

        // Mostra il fragment con il menu
        MenuPazienteFragment menuFragment = MenuPazienteFragment.newInstance(paziente, medico);
        menuFragment.show(getSupportFragmentManager(), "menuFragment");
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
                    Intent intent = new Intent(context, ImpostazioniMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 2){
            btnSos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SosMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 3){
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomeMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 4){
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InviaMessaggio.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 5){
            btnHP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StrumentoBiomedico.class);
                    intent.putExtra("medico", medico);
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
            case "ImpostazioniMedico":
                value = 1;
                break;
            case "SosMedico":
                value = 2;
                break;
            case "HomeMedico":
                value = 3;
                break;
            case "InviaMessaggio":
                value = 4;
                break;
            case "StrumentoBiometrico":
                value = 5;
                break;
        }

        return value;
    }
}
