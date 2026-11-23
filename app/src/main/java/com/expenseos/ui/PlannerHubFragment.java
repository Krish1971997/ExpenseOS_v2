package com.expenseos.ui;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.expenseos.R;
import com.expenseos.util.AppConfig;

public class PlannerHubFragment extends HubFragment {
    @Override protected String pageTitle() { return "Planner"; }
    @Override protected void buildRows(@NonNull Hub hub) {
        AppConfig cfg = AppConfig.get(requireContext()); int bookId = cfg.getActiveBookId();
        hub.section("Calendar and planning");
        hub.row(R.drawable.ic_calendar_check, "Calendar", "Daily activity for " + cfg.getActiveBookName(), () -> {
            Intent i = new Intent(requireContext(), CalendarViewActivity.class); i.putExtra("bookId", bookId); startActivity(i);
        });
        hub.row(R.drawable.ic_add, "Add task", "Tasks with notifications and alarms", () -> startActivity(new Intent(requireContext(), AddTaskActivity.class)));
        hub.section("Reminders and integrations");
        hub.row(R.drawable.ic_schedule, "Events and reminders", "Events, alarm tones, and reminder rules", () -> startActivity(new Intent(requireContext(), RemindersListActivity.class)));
        hub.row(R.drawable.ic_calendar_check, "Google Calendar", "Sync tasks with Google Calendar", () -> startActivity(new Intent(requireContext(), IntegrationsCalendarActivity.class)));
        hub.row(R.drawable.ic_configuration, "Scheduler", "Configure scheduled tasks", () -> startActivity(new Intent(requireContext(), SchedulerActivity.class)));
    }
}
