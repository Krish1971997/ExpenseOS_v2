package com.expenseos.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.expenseos.util.AppConfig;
import com.expenseos.util.AuthManager;
import com.expenseos.util.CatalystClient;

/** Shared account dialog for the app shell and book list. */
public final class AccountUi {
    private AccountUi() {}

    public static void show(Activity activity) {
        int pad = Math.round(24 * activity.getResources().getDisplayMetrics().density);
        TextView account = new TextView(activity);
        account.setTextSize(15);
        account.setPadding(pad / 2, pad / 2, pad / 2, pad / 2);
        account.setText("Loading account…");
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Account")
                .setView(account)
                .setPositiveButton("Close", null)
                .setNegativeButton("Sign out", (d, w) -> confirmSignOut(activity))
                .show();
        Context app = activity.getApplicationContext();
        new Thread(() -> {
            String message;
            try {
                String email = CatalystClient.get(app, "/me").optString("email", "");
                message = email.isEmpty() ? "Signed in with Zoho" : "Signed in with Zoho\n" + email;
            } catch (Exception ex) {
                message = "Signed in with Zoho\n(Account service unavailable)";
            }
            String finalMessage = message;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (dialog.isShowing()) account.setText(finalMessage);
            });
        }).start();
    }

    private static void confirmSignOut(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Sign out?")
                .setMessage("Cloud credentials and API keys will be removed from this phone. Your local data stays. You will sign in with Zoho again to continue.")
                .setPositiveButton("Sign out", (d, w) -> signOut(activity))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void signOut(Activity activity) {
        Context app = activity.getApplicationContext();
        AuthManager.signOut(app, success -> new Handler(Looper.getMainLooper()).post(() -> {
            if (!success) {
                Toast.makeText(app, "Sign out failed. Check internet and try again.", Toast.LENGTH_LONG).show();
                return;
            }
            AppConfig.get(app).clearSecrets();
            Intent login = new Intent(app, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            app.startActivity(login);
        }));
    }
}
