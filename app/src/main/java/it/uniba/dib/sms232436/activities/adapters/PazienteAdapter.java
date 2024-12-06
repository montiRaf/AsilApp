package it.uniba.dib.sms232436.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;

public class PazienteAdapter extends RecyclerView.Adapter<PazienteAdapter.PazienteViewHolder> {

    private List<Paziente> pazienteList;
    private OnPazienteClickListener pazienteClickListener;

    public PazienteAdapter(List<Paziente> pazienteList, OnPazienteClickListener listener) {
        this.pazienteList = pazienteList;
        this.pazienteClickListener = listener;
    }

    @NonNull
    @Override
    public PazienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paziente, parent, false);
        return new PazienteViewHolder(view, pazienteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PazienteViewHolder holder, int position) {
        Paziente paziente = pazienteList.get(position);
        holder.textViewNome.setText(paziente.getNome());
        holder.textViewCognome.setText(paziente.getCognome());
        holder.textViewId.setText(paziente.getId());
        holder.textViewEmail.setText(paziente.getEmail());
        holder.textViewEta.setText(String.valueOf(paziente.getEtà()));
    }

    @Override
    public int getItemCount() {
        return pazienteList.size();
    }

    public static class PazienteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewNome, textViewCognome, textViewId, textViewEta, textViewEmail;
        OnPazienteClickListener pazienteClickListener;

        public PazienteViewHolder(@NonNull View itemView, OnPazienteClickListener listener) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.nomePaziente);
            textViewCognome = itemView.findViewById(R.id.cognomePaziente);
            textViewId = itemView.findViewById(R.id.idPaziente);
            textViewEta = itemView.findViewById(R.id.etaPaziente);
            textViewEmail = itemView.findViewById(R.id.emailPaziente);
            pazienteClickListener = listener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            pazienteClickListener.onPazienteClick(getAdapterPosition());
        }
    }
    public interface OnPazienteClickListener {
        void onPazienteClick(int position);
    }
}
