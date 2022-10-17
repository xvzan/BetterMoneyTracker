package com.xvzan.bettermoneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.ui.exportandimport.ExportDialogFragment;
import com.xvzan.bettermoneytracker.ui.exportandimport.ImportDialogfragment;

import io.realm.Realm;

public class ImportAndExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_and_export);
        boolean hasaccount = false;
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("nowAccount", "");
        try (Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(mAccount.class).findAll().size() > 0) {
                hasaccount = true;
            }
        }
        if (hasaccount) {
            ExportDialogFragment exportDialogFragment = new ExportDialogFragment();
            exportDialogFragment.show(getSupportFragmentManager(), "export_dialog");
        } else {
            ImportDialogfragment importDialogfragment = new ImportDialogfragment();
            importDialogfragment.show(getSupportFragmentManager(), "import_dialog");
        }
    }
}