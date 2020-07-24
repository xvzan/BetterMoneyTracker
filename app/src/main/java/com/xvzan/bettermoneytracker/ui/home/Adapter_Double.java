package com.xvzan.bettermoneytracker.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.icu.util.Currency;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.MainActivity;
import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mTra;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class Adapter_Double extends RecyclerView.Adapter<Adapter_Double.DoubleTraHolder> implements FastScroller.BubbleTextGetter {

    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
    //private NumberFormat numberFormat;
    //private double d_Double;
    private final OrderedRealmCollection<mTra> mTraList;
    private Realm realminstance;

    Adapter_Double(Context context, Realm instance) {
        this.mContext = context;
        //numberFormat = NumberFormat.getCurrencyInstance();
        //d_Double = Math.pow(10d, Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
        realminstance = instance;
        mTraList = realminstance.where(mTra.class).findAllAsync().sort("mDate", Sort.ASCENDING);
    }

    @Override
    public Date getDateToShowInBubble(final int pos) {
        return mTraList.get(pos).getmDate();
    }

    @Override
    @NonNull
    public Adapter_Double.DoubleTraHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DoubleTraHolder(LayoutInflater.from(mContext).inflate(R.layout.transaction_duo, parent, false));
    }

    @Override
    public void onBindViewHolder(Adapter_Double.DoubleTraHolder holder, final int position) {
        holder.format = new DecimalFormat(mTraList.get(position).getUCurrency().getPattern());
        double d_Double = Math.pow(10d, mTraList.get(position).getUCurrency().getFractionalDigits());
        holder.tdDate.setText(sdf.format(mTraList.get(position).getmDate()));
        holder.tdAU.setText(mTraList.get(position).getAccU().getAname());
        holder.tdAB.setText(mTraList.get(position).getAccB().getAname());
        holder.tdUAM.setText(holder.format.format(mTraList.get(position).getuAm() / d_Double));
        if (mTraList.get(position).getuAm() < 0)
            holder.tdUAM.setTextColor(Color.RED);
        else
            holder.tdUAM.setTextColor(holder.tdDate.getTextColors());
        if (mTraList.get(position).hasTask())
            holder.tdDate.setBackgroundTintMode(PorterDuff.Mode.DST);
        else
            holder.tdDate.setBackgroundTintMode(PorterDuff.Mode.SRC);
        holder.tdEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).mTraToEdit = mTraList.get(position);
                Navigation.findNavController(v).navigate(R.id.nav_edit_tran);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTraList.size();
    }

    static class DoubleTraHolder extends RecyclerView.ViewHolder {
        TextView tdDate;
        TextView tdAU;
        TextView tdAB;
        TextView tdUAM;
        ImageButton tdEdit;
        NumberFormat format;

        DoubleTraHolder(View itemView) {
            super(itemView);
            tdDate = itemView.findViewById(R.id.tv_td_date);
            tdAU = itemView.findViewById(R.id.tv_td_aU);
            tdAB = itemView.findViewById(R.id.tv_td_aB);
            tdUAM = itemView.findViewById(R.id.tv_td_uAm);
            tdEdit = itemView.findViewById(R.id.ib_td_edit);
        }
    }
}
