package us.logaming.myufrplanning.ui.planningwidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.model.PlanningItem;
import us.logaming.myufrplanning.data.repositories.PlanningRepository;

public class PlanningRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final Executor executor;

    private List<PlanningItem> planningItems;

    public PlanningRemoteViewsFactory(Context context, Executor executor) {
        this.context = context;
        this.executor = executor;
    }

    @Override
    public void onCreate() {
        fetchPlanning();
    }

    private void fetchPlanning() {
        PlanningRepository planningRepository = new PlanningRepository(this.context, this.executor);
        planningRepository.fetchLatestPlanning(result -> {
            if (result instanceof Result.Success) {
                this.planningItems = ((Result.Success<List<PlanningItem>>) result).data;
            } else {
                this.planningItems = new ArrayList<>();
            }
        }, true);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(this.context.getPackageName(), R.layout.item_planning_widget);

        if (position < this.planningItems.size()) {
            views.setTextViewText(R.id.text_title_planning_widget_item, this.planningItems.get(position).getTitle());
            if (this.planningItems.get(position).isASubject()) {
                views.setTextViewText(R.id.text_hour_planning_widget_item, this.planningItems.get(position).getHour());
                views.setTextViewText(R.id.text_info_1st_row_planning_widget_item, this.planningItems.get(position).getInfo1());

                if (this.planningItems.get(position).getInfo2() != null) {
                    views.setTextViewText(R.id.text_info_2nd_row_planning_widget_item, this.planningItems.get(position).getInfo2());
                }
                else {
                    views.setTextViewText(R.id.text_info_2nd_row_planning_widget_item, "");
                }
            }
            else {
                views.setTextViewText(R.id.text_hour_planning_widget_item, "");
                views.setTextViewText(R.id.text_info_1st_row_planning_widget_item, "");
                views.setTextViewText(R.id.text_info_2nd_row_planning_widget_item, "");
            }
            Bundle extras = new Bundle();
            extras.putInt(PlanningWidgetProvider.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.card_planning_widget_item, fillInIntent);

            try {
                System.out.println("Loading view " + position);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return views;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return this.planningItems.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDataSetChanged() {
        fetchPlanning();
    }

    @Override
    public void onDestroy() {
        this.planningItems.clear();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
