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
import it.uniba.dib.sms232436.classes.Terapia;

public class TerapiaAdapter extends RecyclerView.Adapter<TerapiaAdapter.TerapiaViewHolder> {

    private List<Terapia> terapia;
    private Context context;

    public TerapiaAdapter(Context context, List<Terapia> terapia) {
        this.context = context;
        this.terapia = terapia;
    }

    @NonNull
    @Override
    public TerapiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_terapia, parent, false);
        return new TerapiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TerapiaViewHolder holder, int position) {
        Terapia terapia = this.terapia.get(position);
        holder.nomeDottore.setText(terapia.getNomeDottore());
        holder.cognomeDottore.setText(terapia.getCognomeDottore());
        holder.nomeFarmaco.setText(terapia.getNomeFarmaco());
        holder.dosaggio.setText(terapia.getDosaggio());
        holder.posologia.setText(terapia.getPosologia());
        holder.dataInizio.setText(terapia.getDataInizio());
        holder.dataFine.setText(terapia.getDataFine());

        Log.d("NOME FARMACO", "" + terapia.getNomeFarmaco());
        Log.d("DOSAGGIO", "" + terapia.getDosaggio());

    }

    @Override
    public int getItemCount() {
        return terapia.size();
    }

    public static class TerapiaViewHolder extends RecyclerView.ViewHolder {
        TextView nomeDottore;
        TextView cognomeDottore;
        TextView nomeFarmaco;
        TextView dosaggio;
        TextView posologia;
        TextView dataInizio;
        TextView dataFine;

        public TerapiaViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeDottore = itemView.findViewById(R.id.textViewNomeDottore);
            cognomeDottore = itemView.findViewById(R.id.textViewCognomeDottore);
            nomeFarmaco = itemView.findViewById(R.id.textViewNomeFarmaco);
            dosaggio = itemView.findViewById(R.id.textViewDosaggio);
            posologia = itemView.findViewById(R.id.textViewPosologia);
            dataInizio = itemView.findViewById(R.id.textViewDataInizio);
            dataFine = itemView.findViewById(R.id.textViewDataFine);
        }
    }
}
