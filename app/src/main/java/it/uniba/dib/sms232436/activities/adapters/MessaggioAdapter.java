package it.uniba.dib.sms232436.activities.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Messaggio;

public class MessaggioAdapter extends RecyclerView.Adapter<MessaggioAdapter.MessaggioViewHolder> {

    private List<Messaggio> messaggioList;
    private OnMessaggioClickListener messaggioClickListener;

    public MessaggioAdapter(List<Messaggio> messaggioList, OnMessaggioClickListener listener) {
        this.messaggioList = messaggioList;
        this.messaggioClickListener = listener;
    }

    @NonNull
    @Override
    public MessaggioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messaggio, parent, false);
        return new MessaggioViewHolder(view, messaggioClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessaggioViewHolder holder, int position) {
        Messaggio messaggio = messaggioList.get(position);
        holder.tvMittente.setText(messaggio.getNomeMedico() + " " + messaggio.getCognomeMedico());
        holder.tv_data_invio.setText(messaggio.getDataInvio());

        if (messaggio.isLetto()) {
            holder.tvStatoLettura.setText("Letto");
            holder.tvStatoLettura.setTextColor(Color.GREEN);
        } else {
            holder.tvStatoLettura.setText("Non letto");
            holder.tvStatoLettura.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return messaggioList.size();
    }

    public static class MessaggioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMittente, tvStatoLettura, tv_data_invio;
        OnMessaggioClickListener messaggioClickListener;

        public MessaggioViewHolder(@NonNull View itemView, OnMessaggioClickListener listener) {
            super(itemView);
            tvMittente = itemView.findViewById(R.id.tv_mittente);
            tvStatoLettura = itemView.findViewById(R.id.tv_stato_lettura);
            tv_data_invio = itemView.findViewById(R.id.tv_data_invio);
            messaggioClickListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            messaggioClickListener.onMessaggioClick(getAdapterPosition());
        }
    }

    public interface OnMessaggioClickListener {
        void onMessaggioClick(int position);
    }
}
