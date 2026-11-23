package com.expenseos.ui;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.expenseos.dao.CashBookDao;
import com.expenseos.model.CashBook;
import com.expenseos.util.AppConfig;
import java.util.ArrayList;
import java.util.List;

/** Book selection and management actions for the shell top bar. */
public final class BookSwitcherSheet {
    private BookSwitcherSheet() {}
    public static void show(Activity activity, Runnable onChanged) {
        AppConfig cfg = AppConfig.get(activity);
        List<CashBook> books = new CashBookDao(activity).findAll();
        List<String> names = new ArrayList<>();
        for (CashBook book : books) names.add((book.getId() == cfg.getActiveBookId() ? "✓  " : "") + book.getName());
        names.add("All transactions (all books)");
        names.add("Manage books…");
        new AlertDialog.Builder(activity).setTitle("Switch book")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    if (which < books.size()) {
                        CashBook selected = books.get(which);
                        cfg.setActiveBook(selected.getId(), selected.getName());
                        onChanged.run();
                    } else if (which == books.size()) {
                        activity.startActivity(new Intent(activity, AllTransactionsActivity.class));
                    } else {
                        activity.startActivity(new Intent(activity, MainActivity.class).putExtra("manage", true));
                    }
                }).show();
    }
}
