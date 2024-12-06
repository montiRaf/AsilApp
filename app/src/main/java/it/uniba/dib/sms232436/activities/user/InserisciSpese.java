package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.Spesa;

public class InserisciSpese extends AppCompatActivity {
    private TextView selectedCategoryTextView;
    private String selectedCategory;
    private Paziente paziente;
    private Context context;
    private String CodiceUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spese_inserisci);
        initializeDB();
        initializeBottomBar();
        selectedCategoryTextView = findViewById(R.id.selected_category_textview);
        Button menuButton = findViewById(R.id.category_button);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        CodiceUtente = paziente.getId();

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategorySelectionDialog();
            }
        });

        Button insertButton = findViewById(R.id.buttonInserisciSpese);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    insertSpese();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.spesagg_tit));

    }

    private void initializeDB(){
        FirebaseApp.initializeApp(this);
    }


    private void insertSpese() throws ParseException {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Spese");
        String dateAcq = new SimpleDateFormat("MM/dd/yyyy", Locale.ITALY).format(Calendar.getInstance().getTime());
        EditText fieldValues;
        fieldValues = findViewById(R.id.editTextProdotto);
        String prodName = fieldValues.getText().toString();
        fieldValues = findViewById(R.id.editTextQuantità);
        int quantity = Integer.parseInt(fieldValues.getText().toString());
        fieldValues = findViewById(R.id.editTextImporto);
        String price = fieldValues.getText().toString();
        Spesa spesa = new Spesa(generateRandomString(), CodiceUtente, dateAcq, price, quantity, prodName, selectedCategory);
        db.child(spesa.getCodiceSpesa()).setValue(spesa)
                .addOnSuccessListener(aVoid -> {
                    // Gestione del successo
                    Toast.makeText(InserisciSpese.this, getString(R.string.dati_inseriti_succ), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Gestione dell'errore
                    Toast.makeText(InserisciSpese.this, getString(R.string.err_inserimento_dati), Toast.LENGTH_SHORT).show();
                });
        finish();
    }

    private void showCategorySelectionDialog() {
        final String[] categories = {"Cibo", "Prodotti Sanitari", "Visite Sanitarie", "Svago", "Altro"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleziona una Categoria")
                .setSingleChoiceItems(categories, -1, (dialog, which) -> {
                    selectedCategory = categories[which];
                })
                .setPositiveButton("OK", (dialog, id) -> {
                    if (selectedCategory != null) {
                        selectedCategoryTextView.setText("Categoria selezionata: " + selectedCategory);
                    }
                })
                .setNegativeButton("Annulla", (dialog, id) -> {
                });
        builder.create().show();
    }

    public static String generateRandomString() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int STRING_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }

        return sb.toString();
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
