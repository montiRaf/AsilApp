package it.uniba.dib.sms232436.activities.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Messaggio;
import android.util.Log;

public class MessaggioFragment extends DialogFragment {

    private Messaggio messaggio;

    private DatabaseReference dbMessaggi;

    public static MessaggioFragment newInstance(Messaggio messaggio) {
        MessaggioFragment fragment = new MessaggioFragment();
        Bundle args = new Bundle();
        args.putSerializable("messaggio", messaggio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaggio, container, false);
        // Recupera i dati del messaggio dall'argomento passato
        messaggio = (Messaggio) getArguments().getSerializable("messaggio");

        // Trova e imposta il contenuto del messaggio
        TextView textViewDettagli = view.findViewById(R.id.textViewDettagli);
        textViewDettagli.setText(messaggio.getMessaggio());

        // Inizializza Firebase reference
        dbMessaggi = FirebaseDatabase.getInstance().getReference("Messaggi");

        // Aggiorna lo stato del messaggio a "letto"
        aggiornaStatoMessaggio(messaggio);

        return view;
    }

    private void aggiornaStatoMessaggio(Messaggio messaggio) {
        messaggio.setLetto(true); // Imposta il messaggio come letto

        // Aggiorna il database
        dbMessaggi.child(messaggio.getIdMessaggio()).setValue(messaggio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Messaggio", "Stato aggiornato a letto");
                        } else {
                            Log.e("Messaggio", "Errore nell'aggiornare lo stato", task.getException());
                        }
                    }
                });
    }
}
