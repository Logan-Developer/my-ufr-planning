package us.logaming.myufrplanning.ui.choosegroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.model.GroupItem;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private final List<GroupItem> groupItems;
    private final OnGroupItemClickListener onGroupItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialTextView tvTitle;

        private final OnGroupItemClickListener onGroupItemClickListener;

        public ViewHolder(@NonNull View itemView, OnGroupItemClickListener onGroupItemClickListener) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.text_title_group_item);
            MaterialCardView cardView = itemView.findViewById(R.id.card_group_item);

            this.onGroupItemClickListener = onGroupItemClickListener;

            cardView.setOnClickListener(this);
        }

        public MaterialTextView getTvTitle() {
            return tvTitle;
        }

        @Override
        public void onClick(View v) {
            onGroupItemClickListener.OnGroupItemClick(getAdapterPosition());
        }
    }

    public GroupsAdapter(List<GroupItem> groupItems, OnGroupItemClickListener onGroupItemClickListener) {
        this.groupItems = groupItems;
        this.onGroupItemClickListener = onGroupItemClickListener;
    }

    public interface OnGroupItemClickListener {
        void OnGroupItemClick(int position);
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view, onGroupItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
        holder.getTvTitle().setText(this.groupItems.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.groupItems.size();
    }
}
