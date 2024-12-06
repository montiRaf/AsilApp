package it.uniba.dib.sms232436.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class Registrazione extends AppCompatActivity {
    EditText signupName, signupCognome, signupUsername, signupEmail, signupPassword;
    RadioButton paziente, dottore, sessoMaschio, sessoFemmina;
    String tipoPersona, sesso;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    private EditText dobEditText;
    private Calendar calendar;
    private int eta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        signupName = findViewById(R.id.signup_name);
        signupCognome = findViewById(R.id.signup_cognome);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);

        paziente = findViewById(R.id.signup_paziente);
        dottore = findViewById(R.id.signup_dottore);
        sessoMaschio = findViewById(R.id.signup_sesso_maschio);
        sessoFemmina = findViewById(R.id.signup_sesso_femmina);

        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        dobEditText = findViewById(R.id.signup_dob);
        calendar = Calendar.getInstance();

        // Apre il DatePickerDialog quando si clicca sull'EditText
        dobEditText.setOnClickListener(view -> {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Registrazione.this,
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        // Imposta la data selezionata nell'EditText
                        dobEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);

                        // Calcola l'età
                        eta = calcolaEta(selectedYear, selectedMonth, selectedDay);
                    }, year, month, day);
            datePickerDialog.show();
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controllaRegistrazione()){
                    database = FirebaseDatabase.getInstance();

                    // Dati inseriti dall'utente
                    String name = signupName.getText().toString();
                    String cognome = signupCognome.getText().toString();
                    String email = signupEmail.getText().toString();
                    String username = signupUsername.getText().toString();
                    String password = signupPassword.getText().toString();

                    // Sostituisci eventuali punti con un underscore, in modo che sia valido per firebase
                    String usernameDB = username.replace(".", "_");

                    // Determina il sesso selezionato
                    if (sessoMaschio.isChecked()) {
                        sesso = "Maschio";
                    } else if (sessoFemmina.isChecked()) {
                        sesso = "Femmina";
                    }

                    // Tipologia di persona
                    if (dottore.isChecked()){
                        tipoPersona = "medico";
                        reference = database.getReference("Medici");  // Salva nel database dei medici
                        Medico medico = new Medico(name, cognome, usernameDB, email, password, sesso);
                        reference.child(usernameDB).setValue(medico);
                    }
                    else{
                        tipoPersona = "paziente";
                        reference = database.getReference("Pazienti");  // Salva nel database dei pazienti
                        Paziente paziente = new Paziente(name, cognome, usernameDB, email, password, sesso, eta);
                        reference.child(usernameDB).setValue(paziente);
                    }

                    // Messaggio di conferma
                    Toast.makeText(Registrazione.this, "Ti sei registrato con successo!", Toast.LENGTH_SHORT).show();
                    String qrCodeData = username; // Usa l'username come dati del QR code
                    if (ContextCompat.checkSelfPermission(Registrazione.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Registrazione.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        generateQRCode(qrCodeData);
                    }

                    Toast.makeText(Registrazione.this, "Ti sei registrato con successo!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registrazione.this, Login.class);
                    startActivity(intent);
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registrazione.this, Login.class);
                startActivity(intent);
            }
        });
    }

    // Metodo per calcolare l'età
    private int calcolaEta(int anno, int mese, int giorno) {
        Calendar oggi = Calendar.getInstance();
        int eta = oggi.get(Calendar.YEAR) - anno;

        // Verifica se il compleanno è già passato quest'anno
        if (oggi.get(Calendar.MONTH) < mese || (oggi.get(Calendar.MONTH) == mese && oggi.get(Calendar.DAY_OF_MONTH) < giorno)) {
            eta--;
        }
        return eta;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateQRCode(signupUsername.getText().toString());
            } else {
                Toast.makeText(Registrazione.this, "Permesso di scrittura su Memoria Esterna mancante.", Toast.LENGTH_LONG).show();
            }
        }
    }
 
    private void generateQRCode(String qrCodeData) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // Salva il bitmap in un file nella directory principale della memoria esterna
            File specificDir = new File("", "AsilApp");
            Log.d("PATH: ", String.valueOf(specificDir));
            if (!specificDir.exists()) {
                if (!specificDir.mkdirs()) {
                    //Toast.makeText(Registrazione.this, "Impossibile creare la directory", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            File file = new File(specificDir, qrCodeData + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            // Mostra un messaggio all'utente
            Toast.makeText(Registrazione.this, "QR code generato in: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Dopo aver generato il QR code e salvato i dati dell'utente nel database
        } catch (WriterException e) {
            Toast.makeText(Registrazione.this, "Errore nella generazione del QR code", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(Registrazione.this, "Errore nella scrittura del file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    public boolean controllaRegistrazione(){
        boolean verified = true;
        if (TextUtils.isEmpty(signupName.getText())){
            verified = false;
            signupName.setError("Inserisci un nome.");
        }
        if (TextUtils.isEmpty(signupCognome.getText())){
            verified = false;
            signupCognome.setError("Inserisci un cognome.");
        }
        if (TextUtils.isEmpty(signupUsername.getText())){
            verified = false;
            signupUsername.setError("Inserisci un Username.");
        }
        if (TextUtils.isEmpty(signupPassword.getText())){
            verified = false;
            signupPassword.setError("Inserisci la password.");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(signupEmail.getText().toString()).matches()){
            verified = false;
            signupEmail.setError("Inserisci una mail valida.");
        }
        return verified;

    }
}