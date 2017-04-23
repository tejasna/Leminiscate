package com.leminiscate.transactionsdetail;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.R;
import com.leminiscate.data.Transaction;
import com.leminiscate.utils.UTCConverter;
import java.util.ArrayList;
import java.util.List;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class TransactionsDetailFragment extends Fragment
    implements TransactionsDetailContract.View {

  private TransactionsDetailContract.Presenter mPresenter;

  private TransactionsDetailFragment.TransactionsAdapter mRecyclerAdapter;

  private Unbinder unbinder;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  public TransactionsDetailFragment() {
  }

  public static TransactionsDetailFragment newInstance() {
    return new TransactionsDetailFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mRecyclerAdapter = new TransactionsDetailFragment.TransactionsAdapter(new ArrayList<>(0));
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.transactions_detail_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    swipeRefreshLayout.setOnRefreshListener(() -> mPresenter.loadTransactions(true));

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.addItemDecoration(
        new TransactionsDetailFragment.TransactionDividerItemDecoration(getContext()));
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(mRecyclerAdapter);

    return rootView;
  }

  @Override public void onResume() {
    super.onResume();
    mPresenter.start();
  }

  @Override public void setPresenter(TransactionsDetailContract.Presenter presenter) {
    mPresenter = checkNotNull(presenter);
  }

  @Override public void setLoadingIndicator(boolean active) {
    swipeRefreshLayout.setRefreshing(active);
  }

  @Override public void showTransactions(List<Transaction> transactions) {
    swipeRefreshLayout.setRefreshing(false);
    mRecyclerAdapter.replaceData(transactions);
  }

  @Override public void showNoTransactions() {
    swipeRefreshLayout.setRefreshing(false);
    Toast.makeText(getContext(), getString(R.string.transactions_empty), Toast.LENGTH_SHORT).show();
  }

  @Override public void showLoadingTransactionsError() {
    swipeRefreshLayout.setRefreshing(false);
    Toast.makeText(getContext(), getString(R.string.transactions_error), Toast.LENGTH_SHORT).show();
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    mPresenter.stop();
  }

  private static class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int ITEM = 1;
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
        View view = layoutInflater.inflate(R.layout.transaction_item, parent, false);
        return new TransactionsDetailFragment.TransactionsAdapter.VHTransaction(view);
      }
      throw new RuntimeException(
          parent.getContext().getString(R.string.exception_no_view_type_found));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder.getItemViewType() == ITEM) {
        TransactionsDetailFragment.TransactionsAdapter.VHTransaction transaction =
            (TransactionsDetailFragment.TransactionsAdapter.VHTransaction) holder;
        transaction.title.setText(transactions.get(position).getDescription());
        transaction.amount.setText(transactions.get(position).getAmount());
        transaction.date.setText(android.text.format.DateUtils.getRelativeTimeSpanString(
            transaction.itemView.getContext(),
            UTCConverter.getTimeInMilliseconds(transactions.get(position).getDate()), false));
      }
    }

    @Override public int getItemCount() {
      return transactions.size();
    }

    @Override public int getItemViewType(int position) {
      return ITEM;
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
  }

  class TransactionDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;

    TransactionDividerItemDecoration(Context context) {
      divider = ContextCompat.getDrawable(context, R.drawable.shape_receycler_view_divider);
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

        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }
}
