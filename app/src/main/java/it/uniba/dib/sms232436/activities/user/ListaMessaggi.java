package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Messaggio;
import it.uniba.dib.sms232436.activities.adapters.MessaggioAdapter;
import it.uniba.dib.sms232436.classes.Paziente;

public class ListaMessaggi extends AppCompatActivity implements MessaggioAdapter.OnMessaggioClickListener {

    private RecyclerView recyclerView;
    private MessaggioAdapter messaggioAdapter;
    private List<Messaggio> messaggioList;
    private DatabaseReference messaggiRef;
    private String idPaziente;
    private Paziente paziente;
    private TextView textViewNoMessaggi;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_messaggi);

        recyclerView = findViewById(R.id.recyclerViewMessaggi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewNoMessaggi = findViewById(R.id.textViewNoMessaggi);

        messaggioList = new ArrayList<>();
        messaggioAdapter = new MessaggioAdapter(messaggioList, this);  // 'this' ora si riferisce al listener
        recyclerView.setAdapter(messaggioAdapter);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        // Recupera l'id del paziente loggato
        idPaziente = paziente.getId();

        // Riferimento al database Firebase
        messaggiRef = FirebaseDatabase.getInstance().getReference("Messaggi");

        // Recupera i messaggi per il paziente loggato
        fetchMessaggi();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.message));

        initializeBottomBar();
    }

    // Implementazione del metodo onMessaggioClick
    @Override
    public void onMessaggioClick(int position) {
        Messaggio messaggioSelezionato = messaggioList.get(position);

        // Mostra il fragment con il messaggio
        MessaggioFragment messaggioFragment = MessaggioFragment.newInstance(messaggioSelezionato);
        messaggioFragment.show(getSupportFragmentManager(), "messaggio_fragment");
    }

    private void fetchMessaggi() {
        messaggiRef.orderByChild("idPaziente").equalTo(idPaziente)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messaggioList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Messaggio messaggio = snapshot.getValue(Messaggio.class);
                            messaggioList.add(messaggio);
                        }

                        // Controlla se ci sono messaggi e gestisci la visibilità della TextView
                        if (messaggioList.isEmpty()) {
                            textViewNoMessaggi.setVisibility(View.VISIBLE);  // Mostra il messaggio "Non ci sono messaggi"
                        } else {
                            textViewNoMessaggi.setVisibility(View.GONE);  // Nasconde la TextView
                        }

                        messaggioAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Gestione errori
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
                icon = findViewById(R.id.message_btn);
                icon.setImageResource(R.drawable.ic_message_bold);
                value = 4;
                break;
            case "Misurazioni":
                value = 5;
                break;
        }

        return value;
    }
}

