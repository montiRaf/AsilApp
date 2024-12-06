package it.uniba.dib.sms232436.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Misurazione;

public class MisurazioneAdapter extends RecyclerView.Adapter<MisurazioneAdapter.MisurazioneViewHolder> {

    private List<Misurazione> misurazioniList;

    public MisurazioneAdapter(List<Misurazione> misurazioniList) {
        this.misurazioniList = misurazioniList;
    }

    @NonNull
    @Override
    public MisurazioneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_misurazione, parent, false);
        return new MisurazioneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MisurazioneViewHolder holder, int position) {
        Misurazione misurazione = misurazioniList.get(position);
        holder.tvStrumento.setText(misurazione.getStrumento());
        holder.tvValore.setText(String.valueOf(misurazione.getValore()));
        holder.tvData.setText(misurazione.getData_ora());
    }

    @Override
    public int getItemCount() {
        return misurazioniList.size();
    }

    static class MisurazioneViewHolder extends RecyclerView.ViewHolder {
        TextView tvStrumento, tvValore, tvData;

        MisurazioneViewHolder(View itemView) {
            super(itemView);
            tvStrumento = itemView.findViewById(R.id.tvStrumento);
            tvValore = itemView.findViewById(R.id.tvValore);
            tvData = itemView.findViewById(R.id.tvData);
        }
    }
}

