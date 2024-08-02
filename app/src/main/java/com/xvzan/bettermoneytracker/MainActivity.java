package com.xvzan.bettermoneytracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.addaccount.AddAccountDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.xvzan.bettermoneytracker.ui.exportandimport.ExportDialogFragment;
import com.xvzan.bettermoneytracker.ui.exportandimport.ImportDialogfragment;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements AddAccountDialogFragment.addAccountListener {

    private AppBarConfiguration mAppBarConfiguration;
    public NavController navController;
    NavigationView navigationView;
    List<MenuItem> addItems;
    SharedPreferences sharedPref;
    SharedPreferences.Editor spEditor;
    boolean noEquity;
    public mTra mTraToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navSetUP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("CommitPrefEdits")
    void navSetUP() {
        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share, R.id.nav_empty)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        spEditor = sharedPref.edit();
        reItems();
    }

    boolean menuClick(String acc) {
        spEditor.putString("nowAccount", acc);
        spEditor.commit();
        navController.navigate(R.id.nav_empty);
        navController.navigate(R.id.action_nav_empty_to_nav_home);
        if (findViewById(R.id.drawer_layout) != null){
            ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawers();
        }
        return true;
    }

    public void reItems() {
        addItems = new ArrayList<>();
        MenuItem menuItem = navigationView.getMenu().add(R.id.groupB, Menu.NONE, 0, R.string.all_transactions).setCheckable(true);
        menuItem.setIcon(R.drawable.ic_all_inclusive_black_24dp);
        menuItem.setOnMenuItemClickListener(item -> menuClick(""));
        addItems.add(menuItem);
        try (final Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(mAccount.class).findAll().isEmpty()) {
                noEquity = true;
                return;
            }
            for (mAccount ma : realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING)) {
                if (ma.getAcct() == 4) {
                    MenuItem m = navigationView.getMenu().add(R.id.groupB, Menu.NONE, 1, ma.getAname()).setCheckable(true);
                    m.setIcon(R.drawable.ic_account_balance_wallet_black_24dp);
                    m.setOnMenuItemClickListener(item -> menuClick(Objects.requireNonNull(item.getTitle()).toString()));
                    addItems.add(m);
                    continue;
                }
                MenuItem m = navigationView.getMenu().add(R.id.groupA, Menu.NONE, 0, ma.getAname()).setCheckable(true);
                switch (ma.getAcct()) {
                    case 0:
                        m.setIcon(R.drawable.ic_monetization_on_black_24dp);
                        break;
                    case 1:
                        m.setIcon(R.drawable.ic_credit_card_black_24dp);
                        break;
                    case 2:
                        m.setIcon(R.drawable.ic_archive_black_24dp);
                        break;
                    case 3:
                        m.setIcon(R.drawable.ic_unarchive_black_24dp);
                        break;
                }
                m.setOnMenuItemClickListener(item -> menuClick(Objects.requireNonNull(item.getTitle()).toString()));
                addItems.add(m);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void openNewTransactionMenu(View view) {
        //NewTransaction nt = new NewTransaction();
        navController.navigate(R.id.nav_edit_tran);
    }

    @Override
    public void onAccountsEdited() {
        for (MenuItem item : addItems) {
            navigationView.getMenu().removeItem(item.getItemId());
        }
        reItems();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_io:
                boolean hasaccount = false;
                try (Realm realm = Realm.getDefaultInstance()) {
                    if (!realm.where(mAccount.class).findAll().isEmpty()) {
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
                return true;
            case R.id.action_balances:
                Intent intent = new Intent(this, BalanceActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_currencies:
                startActivity(new Intent(this, CurrenciesActivity.class));
                return true;
            case R.id.action_plantasks:
                startActivity(new Intent(this, PlanTasksActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
