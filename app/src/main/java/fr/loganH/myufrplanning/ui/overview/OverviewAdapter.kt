package fr.loganH.myufrplanning.ui.overview

import android.content.res.Configuration
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import fr.loganH.myufrplanning.R
import fr.loganH.myufrplanning.databinding.ItemPlanningBinding
import fr.loganH.myufrplanning.model.PlanningItem

class OverviewAdapter(
    private val items: List<PlanningItem>
): RecyclerView.Adapter<OverviewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemPlanningBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_planning, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textTitlePlanningItem.text = items[position].title

        if (items[position].isDate) {
            holder.binding.textTitlePlanningItem.text = items[position].title
            holder.binding.textHourPlanningItem.text = ""
            holder.binding.textRoomPlanningItem.text = ""
            holder.binding.textTeacherPlanningItem.text = ""
        }
        else {
            holder.binding.textTitlePlanningItem.text = items[position].title
            holder.binding.textHourPlanningItem.text = items[position].hour
            holder.binding.textRoomPlanningItem.text = items[position].room
            holder.binding.textTeacherPlanningItem.text = items[position].teacher
        }
        applyRightStyleOnCardView(holder, position)
    }

    private fun applyRightStyleOnCardView(holder: ViewHolder, position: Int) {
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (items[position].isDate) {

            if (holder.itemView.context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(com.google.android.material.R.color.material_dynamic_secondary20)
                    )
                } else {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(R.color.card_date_background_dark)
                    )
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(com.google.android.material.R.color.material_dynamic_secondary80)
                    )
                } else {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(R.color.card_date_background)
                    )
                }
            }

            holder.binding.cardPlanningItem.radius = 0f
            layoutParams.setMargins(0, 0, 0, 0)
            holder.binding.dividerPlanningItem.visibility = View.GONE
        }

        else {

            if (holder.itemView.context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(com.google.android.material.R.color.material_dynamic_secondary40)
                    )
                } else {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(R.color.card_subject_background_dark)
                    )
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(com.google.android.material.R.color.material_dynamic_secondary95)
                    )
                } else {
                    holder.binding.cardPlanningItem.setCardBackgroundColor(
                        holder.itemView.context.getColor(R.color.card_subject_background)
                    )
                }
            }

            holder.binding.cardPlanningItem.radius = 25f
            val marginValue: Int = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.margin_medium)
            layoutParams.setMargins(marginValue, marginValue, marginValue, marginValue)
            holder.binding.dividerPlanningItem.visibility = View.VISIBLE
        }

        holder.binding.cardPlanningItem.layoutParams = layoutParams
    }
}