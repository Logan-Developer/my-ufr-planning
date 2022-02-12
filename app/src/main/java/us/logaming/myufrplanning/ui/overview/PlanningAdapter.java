package us.logaming.myufrplanning.ui.overview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.model.PlanningItem;

public class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.ViewHolder> {
    private final List<PlanningItem> planningItems;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView tvSubject, tvHour, tvInfo1Row, tvInfo2Row;
        private final MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.text_title_planning_item);
            tvHour = itemView.findViewById(R.id.text_hour_planning_item);
            tvInfo1Row = itemView.findViewById(R.id.text_info_1st_row_planning_item);
            tvInfo2Row = itemView.findViewById(R.id.text_info_2nd_row_planning_item);
            cardView = itemView.findViewById(R.id.card_planning_item);
        }

        public MaterialTextView getTvSubject() {
            return tvSubject;
        }

        public MaterialTextView getTvHour() {
            return tvHour;
        }

        public MaterialTextView getTvInfo1Row() {
            return tvInfo1Row;
        }

        public MaterialTextView getTvInfo2Row() {
            return tvInfo2Row;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }
    }

    public PlanningAdapter(Context context, List<PlanningItem> planningItems) {
        this.context = context;
        this.planningItems = planningItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.planning_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTvSubject().setText(this.planningItems.get(position).getTitle());
        if (this.planningItems.get(position).isASubject()) {
            holder.getTvHour().setText(this.planningItems.get(position).getHour());
            holder.getTvInfo1Row().setText(this.planningItems.get(position).getInfo1());

            if (this.planningItems.get(position).getInfo2() != null) {
                holder.getTvInfo2Row().setText(this.planningItems.get(position).getInfo2());
            }
            else {
                holder.getTvInfo2Row().setText("");
            }
        }
        else {
            holder.getTvHour().setText("");
            holder.getTvInfo1Row().setText("");
            holder.getTvInfo2Row().setText("");
        }
        applyRightStyleOnCardView(holder, position);
    }

    @SuppressLint("PrivateResource")
    private void applyRightStyleOnCardView(@NonNull ViewHolder holder, int position) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (this.planningItems.get(position).isASubject()) {
            if ((this.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.getCardView().setCardBackgroundColor(this.context.getColor(com.google.android.material.R.color.material_dynamic_secondary95));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.getCardView().setCardBackgroundColor(this.context.getColor(com.google.android.material.R.color.material_dynamic_secondary40));
                }
            }
            holder.getCardView().setRadius(context.getResources().getDimension(com.google.android.material.R.dimen.mtrl_card_corner_radius));
            int marginValue = (int) context.getResources().getDimension(R.dimen.margin_medium);
            layoutParams.setMargins(marginValue, marginValue, marginValue, marginValue);
        }
        else {
            if ((this.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.getCardView().setCardBackgroundColor(this.context.getColor(com.google.android.material.R.color.material_dynamic_secondary80));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.getCardView().setCardBackgroundColor(this.context.getColor(com.google.android.material.R.color.material_dynamic_secondary20));
                }
            }
            holder.getCardView().setRadius(0);
            layoutParams.setMargins(0, 0, 0, 0);
        }
        holder.getCardView().setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return this.planningItems.size();
    }
}
