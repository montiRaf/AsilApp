package it.uniba.dib.sms232436.activities.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Vaccinazione;

public class VaccinazioniAdapter extends RecyclerView.Adapter<VaccinazioniAdapter.VaccinazioneViewHolder> {

    private List<Vaccinazione> vaccinazioni;    // dati da visualizzare
    private Context context;

    public VaccinazioniAdapter(Context context, List<Vaccinazione> vaccinazioni) {
        this.context = context;
        this.vaccinazioni = vaccinazioni;
    }

    // crea la RecyclerView
    @NonNull
    @Override
    public VaccinazioneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaccinazione, parent, false);
        return new VaccinazioneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccinazioneViewHolder holder, int position) {
        Vaccinazione vaccinazione = vaccinazioni.get(position);
        //holder.nomeDottore.setText(vaccinazione.getNomeDottore());
        holder.nomeVaccino.setText(vaccinazione.getVaccino());
        holder.dataVaccino.setText(vaccinazione.getData());
        //holder.idVaccino.setText(vaccinazione.getIdVaccinazione());

        // Verifica se il richiamo è presente
        if (vaccinazione.getRichiamo() != null && !vaccinazione.getRichiamo().isEmpty()) {
            holder.richiamoVaccino.setText(vaccinazione.getRichiamo());
        } else {
            holder.richiamoVaccino.setText(R.string.no_richiamo); // Nascondi se non disponibile
        }

        Log.d("VaccinazioniAdapter", "ID: " + vaccinazione.getIdVaccinazione());
    }

    @Override
    public int getItemCount() {
        return vaccinazioni.size();
    }

    // memorizza le viste che rappresentano un singolo elemento della lista
    public static class VaccinazioneViewHolder extends RecyclerView.ViewHolder {
        //TextView nomeDottore;
        TextView nomeVaccino;
        TextView dataVaccino;
        TextView idVaccino;
        TextView richiamoVaccino;

        public VaccinazioneViewHolder(@NonNull View itemView) {
            super(itemView);
            //nomeDottore = itemView.findViewById(R.id.nomeDottore);
            nomeVaccino = itemView.findViewById(R.id.nomeVaccino);
            dataVaccino = itemView.findViewById(R.id.dataVaccino);
            //idVaccino = itemView.findViewById(R.id.idVaccinazione);
            richiamoVaccino = itemView.findViewById(R.id.richiamoVaccinazione);

        }
    }
}