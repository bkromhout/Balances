package com.bkromhout.balances.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bkromhout.balances.R;
import com.bkromhout.balances.data.models.CategoryFields;
import com.bkromhout.balances.events.ActionEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * Utility class for constructing dialogs.
 */
public class Dialogs {
    /**
     * Shows a simple info dialog using the given {@code title}, {@code text}, and {@code posText} strings.
     * @param ctx     Context to use.
     * @param title   String resource to use for title.
     * @param text    String resource to use for text.
     * @param posText String resource to use for positive button text.
     */
    public static void simpleInfoDialog(final Context ctx, @StringRes final int title, @StringRes final int text,
                                        @StringRes final int posText) {
        simpleInfoDialog(ctx, ctx.getString(title), ctx.getString(text), ctx.getString(posText));
    }

    /**
     * Shows a simple info dialog using the given {@code title}, {@code text}, and {@code posText} strings.
     * @param ctx     Context to use.
     * @param title   String to use for title.
     * @param text    String to use for text.
     * @param posText String to use for positive button text.
     */
    public static void simpleInfoDialog(final Context ctx, final String title, final String text,
                                        final String posText) {
        new MaterialDialog.Builder(ctx)
                .title(title)
                .content(text)
                .positiveText(posText)
                .show();
    }

    /**
     * Shows a simple confirmation dialog using the given {@code title}, {@code text}, and {@code posText} strings. Upon
     * the positive button being clicked, fires an {@link ActionEvent} using the given {@code actionId}.
     * @param ctx      Context to use.
     * @param title    String resource to use for title.
     * @param text     String resource to use for text.
     * @param posText  String resource to use for positive button text.
     * @param actionId Action ID to send if Yes is clicked.
     */
    public static void simpleConfirmDialog(final Context ctx, @StringRes final int title, @StringRes final int text,
                                           @StringRes final int posText, @IdRes final int actionId) {
        simpleConfirmDialog(ctx, ctx.getString(title), ctx.getString(text), ctx.getString(posText), actionId, null);
    }

    /**
     * Shows a simple confirmation dialog using the given {@code title}, {@code text}, and {@code posText} strings. Upon
     * the positive button being clicked, fires an {@link ActionEvent} using the given {@code actionId}.
     * @param ctx      Context to use.
     * @param title    String resource to use for title.
     * @param text     String resource to use for text.
     * @param posText  String resource to use for positive button text.
     * @param actionId Action ID to send if Yes is clicked.
     * @param data     Additional data to send in the posted {@link ActionEvent}.
     */
    public static void simpleConfirmDialog(final Context ctx, @StringRes final int title, @StringRes final int text,
                                           @StringRes final int posText, @IdRes final int actionId, final Object data) {
        simpleConfirmDialog(ctx, ctx.getString(title), ctx.getString(text), ctx.getString(posText), actionId, data);
    }

    /**
     * Shows a simple confirmation dialog using the given {@code title}, {@code text}, and {@code posText} strings. Upon
     * the positive button being clicked, fires an {@link ActionEvent} using the given {@code actionId}.
     * @param ctx      Context to use.
     * @param title    String to use for title.
     * @param text     String to use for text.
     * @param posText  String to use for positive button text.
     * @param actionId Action ID to send if Yes is clicked.
     */
    public static void simpleConfirmDialog(final Context ctx, final String title, final String text,
                                           final String posText, @IdRes final int actionId) {
        simpleConfirmDialog(ctx, title, text, posText, actionId, null);
    }

    /**
     * Shows a simple confirmation dialog using the given {@code title}, {@code text}, and {@code posText} strings. Upon
     * the positive button being clicked, fires an {@link ActionEvent} using the given {@code actionId}.
     * @param ctx      Context to use.
     * @param title    String to use for title.
     * @param text     String to use for text.
     * @param posText  String to use for positive button text.
     * @param actionId Action ID to send if Yes is clicked.
     * @param data     Additional data to send in the posted {@link ActionEvent}.
     */
    public static void simpleConfirmDialog(final Context ctx, final String title, final String text,
                                           final String posText, @IdRes final int actionId, final Object data) {
        new MaterialDialog.Builder(ctx)
                .title(title)
                .content(text)
                .positiveText(posText)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> EventBus.getDefault().post(new ActionEvent(actionId, data)))
                .show();
    }

    /**
     * Show a dialog used to edit information about a {@link com.bkromhout.balances.data.models.Category}.
     * @param ctx         Context to use.
     * @param catName     Category name to pre-fill.
     * @param isCredit    Whether or not the category is credit, used to pre-check a radio button.
     * @param categoryUid The category's unique ID.
     */
    public static void categoryDialog(final Context ctx, final String catName, final boolean isCredit,
                                      final long categoryUid) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_category, null);
        final TextInputLayout etNameLayout = ButterKnife.findById(view, R.id.category_name_layout);
        final TextInputEditText etName = ButterKnife.findById(view, R.id.category_name);
        final RadioGroup rgType = ButterKnife.findById(view, R.id.category_type);

        etName.setText(catName);
        rgType.check(isCredit ? R.id.type_credit : R.id.type_debit);

        new MaterialDialog.Builder(ctx)
                .title(categoryUid == -1 ? R.string.action_new_category : R.string.action_edit_category)
                .customView(view, false)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .onNegative((dialog, which) -> dialog.dismiss())
                .onPositive((dialog, which) -> {
                    // Make sure that a name was supplied.
                    if (etName.getText().toString().trim().isEmpty()) {
                        etNameLayout.setError(ctx.getString(R.string.error_required));
                        return;
                    }

                    // Make a data bundle.
                    Bundle data = new Bundle();
                    data.putString(CategoryFields.NAME, etName.getText().toString().trim());
                    data.putBoolean(CategoryFields.IS_CREDIT, rgType.getCheckedRadioButtonId() == R.id.type_credit);
                    if (categoryUid != -1)
                        data.putLong(CategoryFields.UNIQUE_ID, categoryUid);
                    // Figure out action ID.
                    int actionId = categoryUid == -1 ? R.id.action_new_category : R.id.action_edit_category;

                    // Post event with data.
                    EventBus.getDefault().post(new ActionEvent(actionId, data));
                    dialog.dismiss();
                })
                .show();
    }
}
