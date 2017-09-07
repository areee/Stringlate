package io.github.lonamiwebs.stringlate.classes.locales;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;

import io.github.lonamiwebs.stringlate.R;

public class LocaleSelectionDialog extends DialogFragment {

    //region Static methods and members

    public static final String TAG = "LocaleSelectionDialog";

    public static LocaleSelectionDialog newInstance() {
        LocaleSelectionDialog result = new LocaleSelectionDialog();
        result.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return result;
    }

    //endregion

    //region Members

    private LocaleEntryAdapter mLocaleEntryAdapterLocales;
    private LocaleEntryAdapter mLocaleEntryAdapterCountries;

    private RecyclerView mLocaleRecyclerView;
    private EditText mSearchEditText;

    public interface OnLocaleSelected {
        void onLocaleSelected(Locale which);
    }

    OnLocaleSelected onLocaleSelected;

    //endregion

    //region Creation

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_locale_selection, container, false);

        mLocaleRecyclerView = root.findViewById(R.id.locale_recycler_view);
        mSearchEditText = root.findViewById(R.id.search_edit_text);

        final Dialog dialog = getDialog();
        if (dialog != null) {
            final Window window = dialog.getWindow();
            if (window != null) {
                // Needed so that the dialog resizes when the keyboard is opened
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onSearchChanged();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        root.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocaleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLocaleEntryAdapterLocales = new LocaleEntryAdapter(getActivity(), false);
        mLocaleEntryAdapterCountries = new LocaleEntryAdapter(getActivity(), "");

        mLocaleEntryAdapterLocales.onItemClick = new LocaleEntryAdapter.OnItemClick() {
            @Override
            public void onClick(final Locale which) {
                ArrayList<Locale> locales = LocaleString.getCountriesForLocale(which.getLanguage());
                if (locales.isEmpty()) {
                    // No locale, shouldn't happen since we're only showing valid ones. No-op
                } else if (locales.size() == 1) {
                    // Single locale, no need to show country selection
                    onLocaleSelected(locales.get(0));
                } else {
                    // More than a country is available, so switch to the countries tab
                    mLocaleEntryAdapterCountries.initLocales(locales);
                    mLocaleRecyclerView.setAdapter(mLocaleEntryAdapterCountries);
                }
            }
        };

        mLocaleEntryAdapterCountries.onItemClick = new LocaleEntryAdapter.OnItemClick() {
            @Override
            public void onClick(final Locale which) {
                onLocaleSelected(which);
            }
        };

        mLocaleRecyclerView.setAdapter(mLocaleEntryAdapterLocales);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onLocaleSelected = (OnLocaleSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocaleSelected");
        }
    }

    //endregion

    //region Events

    void onLocaleSelected(final Locale which) {
        onLocaleSelected.onLocaleSelected(which);
        dismiss();
    }

    void onSearchChanged() {
        mLocaleEntryAdapterLocales.setFilter(mSearchEditText.getText().toString());
    }

    //endregion
}
