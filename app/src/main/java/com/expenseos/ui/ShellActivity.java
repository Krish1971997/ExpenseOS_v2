package com.expenseos.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.expenseos.R;
import com.expenseos.scheduler.SchedulerWorker;
import com.expenseos.ui.home.HomeFragment;
import com.expenseos.util.AppConfig;
import com.expenseos.util.UiUtils;

/** New app shell: top-level book/search/account actions and four stable destinations. */
public class ShellActivity extends AppCompatActivity {
    private static final String KEY_TAB = "shell.tab";
    private static final String[] TAGS = {"tab_txn", "tab_insights", "tab_planner", "tab_more"};
    private static final int[][] NAV = {
            {R.id.shellNavTxn, R.id.shellNavTxnIcon, R.id.shellNavTxnLabel},
            {R.id.shellNavInsights, R.id.shellNavInsightsIcon, R.id.shellNavInsightsLabel},
            {R.id.shellNavPlanner, R.id.shellNavPlannerIcon, R.id.shellNavPlannerLabel},
            {R.id.shellNavMore, R.id.shellNavMoreIcon, R.id.shellNavMoreLabel}};
    private int tab;
    private int loadedBookId;
    private TextView tvBook;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (AppConfig.get(this).getActiveBookId() <= 0) {
            startActivity(new Intent(this, MainActivity.class).putExtra("manage", true)); finish(); return;
        }
        setContentView(R.layout.activity_shell);
        UiUtils.styleStatusBar(getWindow(), this);
        getWindow().setBackgroundDrawableResource(R.drawable.aurora_bg);
        tvBook = findViewById(R.id.tvShellBook);
        loadedBookId = AppConfig.get(this).getActiveBookId();
        findViewById(R.id.btnBookSwitcher).setOnClickListener(v -> BookSwitcherSheet.show(this, this::reloadTabs));
        findViewById(R.id.btnShellSearch).setOnClickListener(v -> startActivity(new Intent(this, GlobalSearchActivity.class)));
        findViewById(R.id.btnShellAccount).setOnClickListener(v -> AccountUi.show(this));
        for (int i = 0; i < NAV.length; i++) { final int idx = i; findViewById(NAV[i][0]).setOnClickListener(v -> select(idx)); }
        SchedulerWorker.schedulePeriodic(this);
        SchedulerWorker.ensureMonthlyCategoryReportScheduler(this);
        if (state != null) tab = state.getInt(KEY_TAB, 0);
        select(tab);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (tab != 0) select(0); else { setEnabled(false); getOnBackPressedDispatcher().onBackPressed(); }
            }
        });
    }
    @Override protected void onResume() {
        super.onResume();
        AppConfig cfg = AppConfig.get(this);
        int current = cfg.getActiveBookId();
        if (current <= 0 || new com.expenseos.dao.CashBookDao(this).findById(current) == null) {
            startActivity(new Intent(this, MainActivity.class).putExtra("manage", true));
            finish();
            return;
        }
        if (tvBook != null) tvBook.setText(cfg.getActiveBookName());
        if (current != loadedBookId && current > 0) reloadTabs();
    }
    @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); out.putInt(KEY_TAB, tab); }
    private void select(int index) {
        tab = Math.max(0, Math.min(index, TAGS.length - 1));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        for (int i = 0; i < TAGS.length; i++) {
            Fragment f = fm.findFragmentByTag(TAGS[i]);
            if (i == tab) { if (f == null) tx.add(R.id.shellContainer, create(i), TAGS[i]); else tx.show(f); }
            else if (f != null) tx.hide(f);
        }
        tx.commitAllowingStateLoss(); paintNav();
    }
    private Fragment create(int index) {
        switch (index) {
            case 0: return new HomeFragment();
            case 1: return new InsightsHubFragment();
            case 2: return new PlannerHubFragment();
            default: return new MoreFragment();
        }
    }
    private void paintNav() {
        int on = ContextCompat.getColor(this, R.color.primary), off = ContextCompat.getColor(this, R.color.text_muted);
        for (int i = 0; i < NAV.length; i++) {
            int color = i == tab ? on : off;
            ((ImageView)findViewById(NAV[i][1])).setImageTintList(ColorStateList.valueOf(color));
            ((TextView)findViewById(NAV[i][2])).setTextColor(color);
        }
    }
    /** Recreate tab content after changing books or restoring cloud data. */
    public void reloadTabs() {
        if (tvBook == null) return;
        AppConfig cfg = AppConfig.get(this); loadedBookId = cfg.getActiveBookId(); tvBook.setText(cfg.getActiveBookName());
        FragmentManager fm = getSupportFragmentManager(); FragmentTransaction tx = fm.beginTransaction();
        for (String tag : TAGS) { Fragment f = fm.findFragmentByTag(tag); if (f != null) tx.remove(f); }
        tx.commitNowAllowingStateLoss(); select(tab);
    }
}
