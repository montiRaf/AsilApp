package it.uniba.dib.sms232436.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232436.R;

public class ListaAllergieAdapter extends RecyclerView.Adapter<ListaAllergieAdapter.AllergiaViewHolder> {

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
        public ImageView imageViewAllergiaIcon;

        public AllergiaViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewNomeAllergia = itemView.findViewById(R.id.nomeAllergia);
            imageViewAllergiaIcon = itemView.findViewById(R.id.imageViewAllergiaIcon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public ListaAllergieAdapter(List<String> allergie) {
        listaAllergie = allergie;
    }

    @Override
    public AllergiaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergia, parent, false);
        return new AllergiaViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(AllergiaViewHolder holder, int position) {
        String currentAllergia = listaAllergie.get(position);
        holder.textViewNomeAllergia.setText(currentAllergia);
        holder.imageViewAllergiaIcon.setImageResource(getAllergiaIcon(currentAllergia));
    }

    @Override
    public int getItemCount() {
        return listaAllergie.size();
    }

    private int getAllergiaIcon(String allergia) {
        switch (allergia) {
            case "Arachidi":
                return R.drawable.arachidi;
            case "Banana":
                return R.drawable.banana;
            case "Crostacei":
                return R.drawable.crostacei;
            case "Fragole":
                return R.drawable.fragola;
            case "Glutine":
                return R.drawable.glutine;
            case "Latte":
                return R.drawable.latte;
            case "Lieviti":
                return R.drawable.lieviti;
            case "Mele":
                return R.drawable.mela;
            case "Molluschi":
                return R.drawable.molluschi;
            case "Patate":
                return R.drawable.patate;
            case "Pesca":
                return R.drawable.pesca;
            case "Pesce":
                return R.drawable.pesce;
            case "Polline":
                return R.drawable.polline;
            case "Pomodoro":
                return R.drawable.pomodoro;
            case "Uova":
                return R.drawable.uova;
            default:
                return R.drawable.def_allergy; // Icona di default
        }
    }

}
