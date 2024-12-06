package it.uniba.dib.sms232436.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232436.R;

public class AllergieAdapter extends RecyclerView.Adapter<AllergieAdapter.AllergiaViewHolder> {

    private List<String> listaAllergie;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AllergiaViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNomeAllergia;
        public ImageButton buttonRimuoviAllergia;

        public AllergiaViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewNomeAllergia = itemView.findViewById(R.id.nomeAllergia);
            buttonRimuoviAllergia = itemView.findViewById(R.id.buttonRimuoviAllergia);

            buttonRimuoviAllergia.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public AllergieAdapter(List<String> allergie) {
        listaAllergie = allergie;
    }

    @Override
    public AllergiaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergia_delete, parent, false);
        return new AllergiaViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(AllergiaViewHolder holder, int position) {
        String currentAllergia = listaAllergie.get(position);
        holder.textViewNomeAllergia.setText(currentAllergia);
    }

    @Override
    public int getItemCount() {
        return listaAllergie.size();
    }

    public void removeItem(int position) {
        listaAllergie.remove(position);
        notifyItemRemoved(position);
    }
}
