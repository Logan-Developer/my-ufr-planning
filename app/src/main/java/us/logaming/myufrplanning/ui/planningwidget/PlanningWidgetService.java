package us.logaming.myufrplanning.ui.planningwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.concurrent.Executors;

public class PlanningWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PlanningRemoteViewsFactory(this.getApplicationContext(), Executors.newFixedThreadPool(4));
    }
}