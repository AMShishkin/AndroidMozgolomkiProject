package com.project.golovolomki.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.project.golovolomki.Assistants.DatabaseHelper;
import com.project.golovolomki.R;
import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    private final float TEXT_SIZE_MAX = 5.0f;
    private android.support.v7.widget.SwitchCompat _switchToUp, _switchMoveToLast, _switchTextAlign, _switchTextSize, settings_switch_night_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
        getWindow().setBackgroundDrawable(null);

        InitializeFonts();
        InitializeButtons();

        _switchToUp = (android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_toup);
        _switchMoveToLast = (android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_tolast);
        _switchTextAlign = (android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_text_align);
        _switchTextSize = (android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_text_size);
        settings_switch_night_theme = (android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_night_theme);
    }

    @Override
    public void onResume() {
        super.onResume();

        InitializeSwitchesListeners();
        InitializeTextSize();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

    private void InitializeFonts() {
        // TEXT VIEW FONT
        ((TextView)findViewById(R.id.settings_title)).setTypeface(Typefaces.get(getBaseContext(), "fonts/mainFont.ttf"));
        // SWITCH FONT
        ((android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_toup)).setTypeface(Typefaces.get(getBaseContext(), "fonts/cavia_puzzle.ttf"));
        ((android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_tolast)).setTypeface(Typefaces.get(getBaseContext(), "fonts/cavia_puzzle.ttf"));
        ((android.support.v7.widget.SwitchCompat)findViewById(R.id.settings_switch_text_align)).setTypeface(Typefaces.get(getBaseContext(), "fonts/cavia_puzzle.ttf"));
        // BUTTON FONT
        ((Button)findViewById(R.id.settings_button_reset)).setTypeface(Typefaces.get(getBaseContext(), "fonts/titleItem.ttf"));
    }

    private void InitializeTextSize() {
        // INITIALIZE D.B.
        MainActivity.bdh = new DatabaseHelper(getApplicationContext());
        MainActivity.db = MainActivity.bdh.getReadableDatabase();
        // GET D.B. SETTINGS
        DatabaseHelper.settingsCursor = MainActivity.db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_SETTINGS, null);
        DatabaseHelper.settingsCursor.moveToFirst();
        MainActivity.db.close();

        float _textUpTextSize = 5.0f;

        if (Boolean.valueOf(DatabaseHelper.settingsCursor.getString(10)))
            _textUpTextSize = TEXT_SIZE_MAX;
        else _textUpTextSize *= 0;

        _switchToUp.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.settings_switch_text_size) + _textUpTextSize);
        _switchMoveToLast.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.settings_switch_text_size) + _textUpTextSize);
        _switchTextAlign.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.settings_switch_text_size) + _textUpTextSize);
        _switchTextSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.settings_switch_text_size) + _textUpTextSize);
        settings_switch_night_theme.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.settings_switch_text_size) + _textUpTextSize);

        ((TextView)findViewById(R.id.settings_button_reset)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.action_bar_buttons_text_size) + _textUpTextSize);
    }

    private void InitializeButtons() {
        (findViewById(R.id.settings_button_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder _ad = new AlertDialog.Builder(SettingsActivity.this);
                _ad.setTitle("СБРОСИТЬ НАСТРОЙКИ");
                _ad.setMessage("Настройки приложения будут сброшены.\n\nПродолжить?");
                _ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.db = MainActivity.bdh.getReadableDatabase();
                        MainActivity.bdh.DropSettingsTable(MainActivity.db);
                        MainActivity.bdh.DropFavoriteTable(MainActivity.db);
                        MainActivity.bdh.DropPuzzleTable(MainActivity.db);
                        MainActivity.db.close();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);

                        Toasty.info(getBaseContext(), "НАСТРОЙКИ СБРОШЕНЫ", Toast.LENGTH_LONG, true).show();
                    }
                });

                _ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                _ad.setCancelable(true);
                _ad.create().show();
            }
        });
    }

    private void InitializeSwitchesListeners() {
        View.OnClickListener _onClkListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings_switch_toup:
                        MainActivity.db = MainActivity.bdh.getReadableDatabase();
                        MainActivity.bdh.UpdateSettingsToUp(MainActivity.db, 1, _switchToUp.isChecked());
                        MainActivity.db.close();
                        break;

                    case R.id.settings_switch_tolast:
                        MainActivity.db = MainActivity.bdh.getReadableDatabase();
                        MainActivity.bdh.UpdateSettingsMoveToLast(MainActivity.db, 1, _switchMoveToLast.isChecked());
                        MainActivity.db.close();
                        break;

                    case R.id.settings_switch_text_align:
                        MainActivity.db = MainActivity.bdh.getReadableDatabase();
                        MainActivity.bdh.UpdateSettingsTextAlign(MainActivity.db, 1, _switchTextAlign.isChecked());
                        MainActivity.db.close();
                        break;

                    case R.id.settings_switch_text_size:
                        MainActivity.db = MainActivity.bdh.getReadableDatabase();
                        MainActivity.bdh.UpdateSettingsTextSize(MainActivity.db, 1, _switchTextSize.isChecked());
                        MainActivity.db.close();
                        InitializeTextSize();
                        break;


                }
            }
        };

        // SWITCH LIST SORT
        _switchToUp.setChecked(Boolean.valueOf(DatabaseHelper.settingsCursor.getString(4)));
        _switchToUp.setOnClickListener(_onClkListener);
        // SWITCH MOVE TO LAST
        _switchMoveToLast.setChecked(Boolean.valueOf(DatabaseHelper.settingsCursor.getString(6)));
        _switchMoveToLast.setOnClickListener(_onClkListener);
        // SWITCH TEXT ALIGN
        _switchTextAlign.setChecked(Boolean.valueOf(DatabaseHelper.settingsCursor.getString(9)));
        _switchTextAlign.setOnClickListener(_onClkListener);
        // SWITCH TEXT SIZE
        _switchTextSize.setChecked(Boolean.valueOf(DatabaseHelper.settingsCursor.getString(10)));
        _switchTextSize.setOnClickListener(_onClkListener);
    }
}