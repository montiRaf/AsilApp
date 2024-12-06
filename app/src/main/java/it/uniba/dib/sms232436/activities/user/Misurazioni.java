package it.uniba.dib.sms232436.activities.user;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Misurazione;
import it.uniba.dib.sms232436.activities.adapters.MisurazioneAdapter;
import it.uniba.dib.sms232436.classes.Paziente;

public class Misurazioni extends AppCompatActivity {

    private RecyclerView recyclerViewMisurazioni;
    private LineChart lineChart;
    private MisurazioneAdapter misurazioneAdapter;
    private List<Misurazione> misurazioniList;
    private String idPaziente;
    private Paziente paziente;
    private TextView noMisurazioniText;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misurazioni);

        // Inizializzazione
        recyclerViewMisurazioni = findViewById(R.id.recyclerViewMisurazioni);
        lineChart = findViewById(R.id.lineChart);
        misurazioniList = new ArrayList<>();
        noMisurazioniText = findViewById(R.id.no_misurazioni_text);

        // Configurazione RecyclerView
        misurazioneAdapter = new MisurazioneAdapter(misurazioniList);
        recyclerViewMisurazioni.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMisurazioni.setAdapter(misurazioneAdapter);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
            Log.d("Nome Paziente", "" + paziente.getNome());
        }

        idPaziente = paziente.getId();

        // Carica misurazioni dal database
        caricaMisurazioni();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.measurements));

        initializeBottomBar();
    }

    private void caricaMisurazioni() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Misurazioni");
        databaseReference.orderByChild("id_paziente").equalTo(idPaziente).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Misurazione misurazione = snapshot.getValue(Misurazione.class);
                    if (misurazione != null) {
                        misurazioniList.add(misurazione);
                    }
                }
                if (misurazioniList.isEmpty()) {
                    // Mostra la TextView con il messaggio e nascondi RecyclerView e LineChart
                    noMisurazioniText.setVisibility(View.VISIBLE);
                    recyclerViewMisurazioni.setVisibility(View.GONE);
                    lineChart.setVisibility(View.GONE);
                } else {
                    // Nasconde la TextView e mostra RecyclerView e LineChart se ci sono misurazioni
                    noMisurazioniText.setVisibility(View.GONE);
                    recyclerViewMisurazioni.setVisibility(View.VISIBLE);
                    lineChart.setVisibility(View.VISIBLE);
                    visualizzaGrafico(); // Chiama il metodo per visualizzare il grafico
                }

                misurazioneAdapter.notifyDataSetChanged(); // Notifica l'adapter per aggiornare la lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Misurazioni.this, "Errore nel recupero delle misurazioni.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void visualizzaGrafico() {
        Map<String, List<Entry>> entriesMap = new HashMap<>();
        Map<String, Integer> colorsMap = new HashMap<>();

        // Assegna colori a ciascun tipo di strumento
        colorsMap.put(getString(R.string.termometro), Color.RED);
        colorsMap.put(getString(R.string.glucometro), Color.BLUE);
        colorsMap.put(getString(R.string.bilancia), Color.WHITE);
        colorsMap.put(getString(R.string.pulsossimetro), Color.MAGENTA);
        colorsMap.put(getString(R.string.elettrocardiografo), Color.BLACK);

        // Popola i dati nel grafico
        for (Misurazione misurazione : misurazioniList) {
            String tipoStrumento = misurazione.getStrumento();
            if (tipoStrumento == null || misurazione.getValore() == 0.0) {
                continue; // Salta se il tipo di strumento o il valore sono null
            }

            if (!entriesMap.containsKey(tipoStrumento)) {
                entriesMap.put(tipoStrumento, new ArrayList<>());
            }
            List<Entry> entryList = entriesMap.get(tipoStrumento);
            entryList.add(new Entry(entryList.size(), (float) misurazione.getValore())); // Usa la dimensione della lista per l'asse x
        }

        // Crea il dataset per ciascun strumento
        List<LineDataSet> dataSets = new ArrayList<>();
        for (Map.Entry<String, List<Entry>> entry : entriesMap.entrySet()) {
            String tipoStrumento = entry.getKey();
            List<Entry> entryList = entry.getValue();

            // Verifica che ci siano dati per questo strumento
            if (!entryList.isEmpty()) {
                Integer color = colorsMap.get(tipoStrumento);
                if (color == null) {
                    color = Color.GRAY; // Colore di default se non trovato
                }
                LineDataSet dataSet = new LineDataSet(entryList, tipoStrumento);
                dataSet.setColor(color);
                dataSet.setValueTextColor(Color.BLACK);

                // Imposta lo spessore della linea per renderla più visibile
                dataSet.setLineWidth(3f);

                dataSets.add(dataSet);
            }
        }

        // Combina i dataset in un unico LineData
        LineData lineData = new LineData(dataSets.toArray(new LineDataSet[0])); // Converti in array
        lineChart.setData(lineData);
        lineChart.getLegend().setEnabled(true); // Abilita la legenda
        lineChart.invalidate(); // Ricarica il grafico
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
                icon = findViewById(R.id.hp_btn);
                icon.setImageResource(R.drawable.ic_misurazioni_bold);
                value = 5;
                break;
        }

        return value;
    }
}

