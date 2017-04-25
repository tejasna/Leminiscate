package com.leminiscate.transactions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.R;
import com.leminiscate.balance.BalanceActivity;
import com.leminiscate.data.Transaction;
import com.leminiscate.transactionsdetail.TransactionsDetailActivity;
import com.leminiscate.utils.CurrencyUtil;
import com.leminiscate.utils.UTCUtil;
import java.util.ArrayList;
import java.util.List;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class TransactionsFragment extends Fragment implements TransactionsContract.View {

  private TransactionsContract.Presenter presenter;

  private TransactionsAdapter recyclerAdapter;

  private Unbinder unbinder;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  public TransactionsFragment() {
  }

  public static TransactionsFragment newInstance() {
    return new TransactionsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    recyclerAdapter = new TransactionsAdapter(new ArrayList<>(0));
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.transactions_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTransactions(true));

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.addItemDecoration(new TransactionDividerItemDecoration(getContext()));
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(recyclerAdapter);

    return rootView;
  }

  @Override public void onStart() {
    super.onStart();
    presenter.start();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    presenter.stop();
  }

  @Override public void setPresenter(TransactionsContract.Presenter presenter) {
    this.presenter = checkNotNull(presenter);
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  @Override public void setLoadingIndicator(boolean active) {
    swipeRefreshLayout.setRefreshing(active);
  }

  @Override public void showTransactions(List<Transaction> transactions) {
    swipeRefreshLayout.setRefreshing(false);
    recyclerAdapter.replaceData(transactions);
  }

  @Override public void showNoTransactions() {
    swipeRefreshLayout.setRefreshing(false);
    Snackbar.make(swipeRefreshLayout, getString(R.string.transactions_empty), Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public void showLoadingTransactionsError() {
    swipeRefreshLayout.setRefreshing(false);
    Snackbar.make(swipeRefreshLayout, getString(R.string.transactions_error), Snackbar.LENGTH_SHORT)
        .show();
  }

  private static class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int HEADER = 1;
    private static int ITEM = 2;
    private List<Transaction> transactions;

    TransactionsAdapter(List<Transaction> transactions) {
      setList(transactions);
    }

    private void setList(List<Transaction> transactions) {
      this.transactions = checkNotNull(transactions);
    }

    private void replaceData(List<Transaction> transactions) {
      setList(transactions);
      notifyDataSetChanged();
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == ITEM) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transactions_item, parent, false);
        return new TransactionsAdapter.VHTransaction(view);
      } else if (viewType == HEADER) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transactions_header, parent, false);
        return new TransactionsAdapter.VHHeader(view);
      }
      throw new RuntimeException(
          parent.getContext().getString(R.string.exception_no_view_type_found));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder.getItemViewType() == ITEM) {
        VHTransaction transaction = (VHTransaction) holder;
        int finalPosition = position - 1;
        transaction.title.setText(transactions.get(finalPosition).getDescription());
        transaction.amount.setText(transactions.get(finalPosition).getAmountInNativeRate());
        int id = CurrencyUtil.map(transactions.get(finalPosition).getCurrency());
        transaction.currency.setBackgroundDrawable(
            ContextCompat.getDrawable(transaction.itemView.getContext(), id));
        transaction.date.setText(android.text.format.DateUtils.getRelativeTimeSpanString(
            transaction.itemView.getContext(),
            UTCUtil.getTimeInMilliseconds(transactions.get(finalPosition).getDate()), false));
      }
    }

    @Override public int getItemCount() {
      return transactions.size() + 1;
    }

    @Override public int getItemViewType(int position) {
      if (position == 0) {
        return HEADER;
      } else {
        return ITEM;
      }
    }

    private class VHTransaction extends RecyclerView.ViewHolder {

      View itemView;
      AppCompatTextView title;
      AppCompatTextView amount;
      AppCompatTextView date;
      AppCompatImageView currency;

      VHTransaction(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.title = (AppCompatTextView) itemView.findViewById(R.id.title);
        this.amount = (AppCompatTextView) itemView.findViewById(R.id.amount);
        this.date = (AppCompatTextView) itemView.findViewById(R.id.date);
        this.currency = (AppCompatImageView) itemView.findViewById(R.id.img_currency);
      }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

      View itemView;
      View balanceHeader;
      View activityHeader;

      VHHeader(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.balanceHeader = itemView.findViewById(R.id.view_group_balance);
        this.activityHeader = itemView.findViewById(R.id.view_group_activity);
        this.balanceHeader.setOnClickListener(view -> {
          Context context = view.getContext();
          Intent intent = new Intent(context, BalanceActivity.class);
          context.startActivity(intent);
        });
        this.activityHeader.setOnClickListener(view -> {
          Context context = view.getContext();
          Intent intent = new Intent(context, TransactionsDetailActivity.class);
          context.startActivity(intent);
        });
      }
    }
  }

  class TransactionDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;

    TransactionDividerItemDecoration(Context context) {
      divider = ContextCompat.getDrawable(context, R.drawable.shape_rv_divider);
    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
      int left = parent.getPaddingLeft();
      int right = parent.getWidth() - parent.getPaddingRight();

      int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
        View child = parent.getChildAt(i);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + divider.getIntrinsicHeight();

        int position = parent.getChildAdapterPosition(child);
        int viewType = parent.getAdapter().getItemViewType(position);

        if (viewType == TransactionsAdapter.HEADER) {
          divider.setBounds(0, 0, 0, bottom);
          divider.draw(c);
        } else {
          divider.setBounds(left, top, right, bottom);
          divider.draw(c);
        }
      }
    }
  }
}
