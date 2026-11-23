package com.expenseos.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.expenseos.R;
import com.expenseos.android.timesheet.TimesheetActivity;
import com.expenseos.util.AppConfig;

public class MoreFragment extends HubFragment {
    private final ActivityResultLauncher<String> smsPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startActivity(new Intent(requireContext(), PassbookActivity.class));
                else Toast.makeText(requireContext(), "SMS permission is needed to read bank messages", Toast.LENGTH_LONG).show();
            });
    @Override protected String pageTitle() { return "More"; }
    @Override protected void buildRows(@NonNull Hub hub) {
        AppConfig cfg = AppConfig.get(requireContext()); int bookId = cfg.getActiveBookId(); String book = cfg.getActiveBookName();
        hub.section("Assistant and capture");
        hub.row(R.drawable.ic_ai, "AI Assistant", "Ask questions about your spending", () -> open(ChatActivity.class));
        hub.row(R.drawable.ic_passbook, "Passbook", "Review bank SMS entries", this::openPassbook);
        hub.row(R.drawable.ic_edit_multi, "Bulk add", "Enter many transactions at once", () -> open(BulkAddActivity.class));
        hub.row(R.drawable.ic_excel, "Import from Excel", "Fetch and import from WorkDrive", () -> open(ImportExcelActivity.class));
        hub.row(R.drawable.ic_food, "Food Tracker", null, () -> open(FoodTrackerActivity.class));
        hub.row(R.drawable.ic_schedule, "Timesheet", "Odoo timesheet", () -> open(TimesheetActivity.class));
        hub.section("Data");
        hub.row(R.drawable.ic_backup, "Backup", "Create or restore local backups", () -> open(BackupActivity.class));
        hub.row(R.drawable.ic_link, "Restore from cloud", "Download your cloud data", () -> confirmRestore());
        hub.row(R.drawable.ic_recycle_bin, "Recycle bin", "Deleted items", () -> { Intent i = new Intent(requireContext(), RecycleBinActivity.class); i.putExtra("bookId", bookId); startActivity(i); });
        hub.row(R.drawable.ic_log, "Audit log", "Changes made to transactions", () -> open(AuditLogActivity.class));
        hub.row(R.drawable.ic_sort, "Sequences", "ID counters per table", () -> open(SequenceActivity.class));
        hub.section("Setup");
        hub.row(R.drawable.ic_settings, "Book setup", "Categories, fields, and keywords for " + book, () -> {
            Intent i = new Intent(requireContext(), SettingsActivity.class); i.putExtra("bookScoped", true); i.putExtra("bookId", bookId); startActivity(i);
        });
        hub.row(R.drawable.ic_configuration, "App settings", "Configuration, alarms, and app lock", () -> open(ConfigActivity.class));
        hub.row(R.drawable.ic_scheduler, "Automation", "Background sync and backup jobs", () -> open(SchedulerActivity.class));
        hub.section("Account");
        hub.row(R.drawable.ic_account, "Account and sign out", "Zoho account", () -> AccountUi.show(requireActivity()));
        hub.section("Developer");
        hub.row(R.drawable.ic_console, "Console log", "Sync and app log", () -> open(LogActivity.class));
        hub.row(R.drawable.ic_sql_console, "SQL console", "Local database queries", () -> open(SqlConsoleActivity.class));
    }
    private void open(Class<?> activity) { startActivity(new Intent(requireContext(), activity)); }
    private void openPassbook() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) open(PassbookActivity.class);
        else smsPermission.launch(Manifest.permission.READ_SMS);
    }
    private void confirmRestore() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Restore from Cloud")
                .setMessage("This downloads your cash books, categories, and transactions from the cloud. Existing entries with matching IDs are replaced by the cloud copy.")
                .setPositiveButton("Restore", (d, w) -> RestoreProgressDialog.run(requireActivity(), ok -> {
                    if (ok && getActivity() instanceof ShellActivity) ((ShellActivity) getActivity()).reloadTabs();
                }))
                .setNegativeButton("Cancel", null).show();
    }
}
