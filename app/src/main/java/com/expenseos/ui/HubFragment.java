package com.expenseos.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.expenseos.R;

/** Scrollable, grouped destination list used by the shell's hub tabs. */
public abstract class HubFragment extends Fragment {
    private ScrollView scroll;
    private LinearLayout content;
    protected abstract String pageTitle();
    protected abstract void buildRows(@NonNull Hub hub);

    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle state) {
        Context c = requireContext();
        scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        content = new LinearLayout(c);
        content.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(c, 16);
        content.setPadding(pad, pad, pad, pad * 2);
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        rebuild();
        return scroll;
    }
    @Override public void onResume() { super.onResume(); rebuild(); }
    private void rebuild() {
        if (content == null || !isAdded()) return;
        int y = scroll.getScrollY();
        content.removeAllViews();
        Hub hub = new Hub(requireContext(), content);
        hub.title(pageTitle());
        buildRows(hub);
        scroll.post(() -> scroll.scrollTo(0, y));
    }
    static int dp(Context c, int v) { return Math.round(v * c.getResources().getDisplayMetrics().density); }

    public static final class Hub {
        private final Context ctx;
        private final LinearLayout parent;
        private LinearLayout card;
        Hub(Context ctx, LinearLayout parent) { this.ctx = ctx; this.parent = parent; }
        void title(String value) {
            TextView t = text(R.style.Text_Headline, value);
            t.setPadding(dp(ctx, 4), 0, 0, dp(ctx, 4));
            parent.addView(t);
        }
        public void section(String value) {
            TextView t = text(R.style.Text_Overline, value);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.topMargin = dp(ctx, 18); lp.bottomMargin = dp(ctx, 6); lp.setMarginStart(dp(ctx, 4));
            parent.addView(t, lp);
            card = new LinearLayout(ctx); card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card);
            parent.addView(card, new LinearLayout.LayoutParams(-1, -2));
        }
        public void row(int iconRes, String title, @Nullable String subtitle, Runnable action) {
            if (card == null) section("");
            if (card.getChildCount() > 0) {
                View divider = new View(ctx); divider.setBackgroundColor(ContextCompat.getColor(ctx, R.color.divider));
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, Math.max(1, HubFragment.dp(ctx, 1)));
                dp.setMarginStart(HubFragment.dp(ctx, 68)); card.addView(divider, dp);
            }
            LinearLayout row = new LinearLayout(ctx); row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(ctx, 14), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12));
            TypedValue tv = new TypedValue(); ctx.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
            row.setBackgroundResource(tv.resourceId); row.setClickable(true); row.setFocusable(true);
            row.setOnClickListener(v -> { if (action != null) action.run(); });
            ImageView icon = new ImageView(ctx); icon.setImageResource(iconRes);
            icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primary)));
            icon.setBackgroundResource(R.drawable.bg_icon_tile_primary);
            int p = dp(ctx, 9); icon.setPadding(p,p,p,p);
            row.addView(icon, new LinearLayout.LayoutParams(dp(ctx,40), dp(ctx,40)));
            LinearLayout col = new LinearLayout(ctx); col.setOrientation(LinearLayout.VERTICAL);
            col.addView(text(R.style.Text_Body, title));
            if (subtitle != null && !subtitle.isEmpty()) col.addView(text(R.style.Text_Caption, subtitle));
            LinearLayout.LayoutParams cl = new LinearLayout.LayoutParams(0, -2, 1f); cl.setMarginStart(dp(ctx,14)); row.addView(col, cl);
            TextView chevron = text(R.style.Text_Title, "›"); chevron.setTextSize(22); chevron.setTextColor(ContextCompat.getColor(ctx, R.color.text_hint));
            row.addView(chevron); card.addView(row);
        }
        private TextView text(int style, String value) { TextView t = new TextView(ctx, null, 0, style); t.setText(value); return t; }
    }
}
