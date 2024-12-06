package it.uniba.dib.sms232436.activities.doctor;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;

public class MenuPazienteFragment extends DialogFragment {

    private Paziente paziente;
    private Medico medico;

    public static MenuPazienteFragment newInstance(Paziente paziente, Medico medico) {
        MenuPazienteFragment fragment = new MenuPazienteFragment();
        Bundle args = new Bundle();
        args.putSerializable("paziente", paziente);
        args.putSerializable("medico", medico);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_paziente, container, false);

        if (getArguments() != null) {
            paziente = (Paziente) getArguments().getSerializable("paziente");
            medico = (Medico) getArguments().getSerializable("medico");
        }

        // Button per Inserisci Terapia
        Button buttonInserisciTerapia = view.findViewById(R.id.buttonInserisciTerapia);
        buttonInserisciTerapia.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InserisciTerapia.class);
            // Passa l'ID del paziente all'activity
            intent.putExtra("paziente_id", paziente.getId());
            intent.putExtra("paziente_nome", paziente.getNome());
            intent.putExtra("paziente_cognome", paziente.getCognome());
            intent.putExtra("medico_nome", medico.getNome());
            intent.putExtra("medico_cognome", medico.getCognome());
            startActivity(intent);
        });

        // Button per Inserisci Allergie
        Button buttonInserisciAllergie = view.findViewById(R.id.buttonInserisciAllergie);
        buttonInserisciAllergie.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InserisciAllergie.class);
            // Passa l'ID del paziente all'activity
            intent.putExtra("paziente_id", paziente.getId());
            intent.putExtra("paziente_nome", paziente.getNome());
            intent.putExtra("paziente_cognome", paziente.getCognome());
            startActivity(intent);
        });

        // Button per Inserisci Vaccinazione
        Button buttonInserisciVaccinazione = view.findViewById(R.id.buttonInserisciVaccinazione);
        buttonInserisciVaccinazione.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InserisciVaccinazione.class);
            // Passa l'ID del paziente all'activity
            intent.putExtra("paziente_id", paziente.getId());
            intent.putExtra("medico_nome", medico.getNome());
            intent.putExtra("medico_cognome", medico.getCognome());
            startActivity(intent);
        });

        return view;
    }
}
