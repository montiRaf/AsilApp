package it.uniba.dib.sms232436.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.activities.doctor.HomeMedico;
import it.uniba.dib.sms232436.activities.user.HomePaziente;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;

public class Login extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton, qrButton;
    TextView signupRedirectText;
    TextView forgotPassword;

    boolean passwordVisible = false; // stato della visibilità della password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        qrButton = findViewById(R.id.qr_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);

        SpannableString content = new SpannableString("Recupera password");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassword.setText(content);

        String logNotRegText = getString(R.string.log_notreg);
        SpannableString contentSignUp = new SpannableString(logNotRegText);
        contentSignUp.setSpan(new UnderlineSpan(), 0, contentSignUp.length(), 0);
        signupRedirectText.setText(contentSignUp);


        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(Login.this, PasswordDimenticata.class)));

        // Aggiungi un listener sull'icona di visibilità della password
        loginPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Indice del drawable a destra
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (loginPassword.getRight() - loginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if (!validateUsername() | !validatePassword()) {
                } else {
                    checkUser();
                }
            }*/
                if (isOnline()) {
                    if (!validateUsername() | !validatePassword()) {
                        // Errore di validazione, non procedere
                    } else {
                        checkUser();
                    }
                } else {
                    Toast.makeText(Login.this, "È necessaria una connessione a Internet per accedere.", Toast.LENGTH_LONG).show();
                }
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Login.this, QRLogin.class);
                startActivity(intent);
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registrazione.class);
                startActivity(intent);
            }
        });
    }

    // Metodo per controllare la connessione a Internet
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Metodo per alternare la visibilità della password
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Nascondi la password
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_baseline_visibility_off_24, 0);
        } else {
            // Mostra la password
            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_baseline_visibility_24, 0);
        }
        passwordVisible = !passwordVisible;
        // Per mantenere il cursore alla fine del testo
        loginPassword.setSelection(loginPassword.getText().length());
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("La mail non può essere vuota.");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("La password non può essere vuota.");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userName = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        // Sostituzione del carattere '.' con '_'
        String processedUserName = userName.replace(".", "_");

        // Riferimento al database Pazienti
        DatabaseReference pazientiReference = FirebaseDatabase.getInstance().getReference("Pazienti");

        pazientiReference.child(processedUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null);
                    // Recupera direttamente il valore di "password" dal nodo
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        String usernameFromDB = snapshot.child("username").getValue(String.class);
                        String nomeFromDB = snapshot.child("nome").getValue(String.class);
                        String cognomeFromDB = snapshot.child("cognome").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);
                        Integer etàFromDB = snapshot.child("età").getValue(Integer.class);  // Verifica che il valore esista
                        String idFromDB = snapshot.child("id").getValue(String.class);
                        String indirizzoFromDB = snapshot.child("indirizzo").getValue(String.class);
                        String pswFromDB = snapshot.child("password").getValue(String.class);
                        String medico_curante = snapshot.child("medico_curante").getValue(String.class);
                        String sesso = snapshot.child("sesso").getValue(String.class);

                        // Crea un'istanza del paziente con i dati recuperati
                        Paziente persona = new Paziente(idFromDB, usernameFromDB, nomeFromDB, cognomeFromDB,
                                etàFromDB != null ? etàFromDB : 0, indirizzoFromDB,
                                emailFromDB, pswFromDB, medico_curante, sesso);

                        // Intent alla home del paziente
                        Intent intent = new Intent(Login.this, HomePaziente.class);
                        intent.putExtra("paziente", persona);
                        startActivity(intent);
                        finish();
                    } else {
                        loginPassword.setError("Credenziali non valide.");
                        loginPassword.requestFocus();
                    }
                } else {
                    // Se non è stato trovato nei Pazienti, controlla nei Medici
                    checkMedici(processedUserName, userPassword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Errore nel recupero dei dati: " + error.getMessage());
            }
        });
    }


    private void checkMedici(String userName, String userPassword) {
        DatabaseReference mediciReference = FirebaseDatabase.getInstance().getReference("Medici");

        mediciReference.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // L'utente esiste tra i medici
                    loginUsername.setError(null);
                    // Recupera il valore della password dal nodo "password"
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        // Se la password è corretta
                        loginUsername.setError(null);
                        // Recupera i dati dell'utente medico
                        String usernameFromDB = snapshot.child("username").getValue(String.class);
                        String nomeFromDB = snapshot.child("nome").getValue(String.class);
                        String cognomeFromDB = snapshot.child("cognome").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);
                        String idFromDB = snapshot.child("id").getValue(String.class);
                        String numTelefonoFromDB = snapshot.child("numTelefono").getValue(String.class);
                        String sesso = snapshot.child("sesso").getValue(String.class);

                        // Crea un oggetto Medico con i dati recuperati
                        Medico medico = new Medico(idFromDB, usernameFromDB, nomeFromDB, cognomeFromDB, numTelefonoFromDB, emailFromDB, passwordFromDB, sesso);

                        // Intent alla home del medico
                        Intent intent = new Intent(Login.this, HomeMedico.class);
                        intent.putExtra("medico", medico);
                        startActivity(intent);
                        finish();
                    } else {
                        // Password non corretta
                        loginPassword.setError("Credenziali non valide.");
                        loginPassword.requestFocus();
                    }
                } else {
                    // L'utente non esiste tra i medici
                    loginUsername.setError("Questo utente non esiste.");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestisci eventuali errori nel recupero dei dati
            }
        });
    }

}