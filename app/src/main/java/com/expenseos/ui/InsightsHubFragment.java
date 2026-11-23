package com.expenseos.ui;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.expenseos.R;
import com.expenseos.util.AppConfig;

public class InsightsHubFragment extends HubFragment {
    @Override protected String pageTitle() { return "Insights"; }
    @Override protected void buildRows(@NonNull Hub hub) {
        AppConfig cfg = AppConfig.get(requireContext());
        int bookId = cfg.getActiveBookId(); String book = cfg.getActiveBookName();
        hub.section("Spending");
        hub.row(R.drawable.ic_stats, "Stats", "Charts and totals for " + book, () -> {
            Intent i = new Intent(requireContext(), StatsActivity.class); i.putExtra("scopeBookId", bookId); startActivity(i);
        });
        hub.row(R.drawable.ic_reports, "Month insights", "Compare spending with last month", () -> startActivity(new Intent(requireContext(), InsightsActivity.class)));
        hub.row(R.drawable.ic_pdf, "Monthly category report", "Category totals for " + book, () -> {
            Intent i = new Intent(requireContext(), MonthlyCategoryReportActivity.class); i.putExtra("bookId", bookId); startActivity(i);
        });
        hub.section("Reports and budgets");
        hub.row(R.drawable.ic_export, "Generate report", "Export your transactions", () -> startActivity(new Intent(requireContext(), GenerateReportActivity.class)));
        hub.row(R.drawable.ic_budget, "Budget", "Limits and progress for " + book, () -> {
            Intent i = new Intent(requireContext(), BudgetActivity.class); i.putExtra("bookId", bookId); startActivity(i);
        });
        hub.row(R.drawable.ic_configuration, "Budget template", "Configure reusable budget categories", () -> startActivity(new Intent(requireContext(), BudgetConfigActivity.class)));
    }
}
