package com.leminiscate.currency;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.leminiscate.data.Currency;
import com.leminiscate.utils.CurrencyClickListener;
import com.leminiscate.utils.CurrencyMapper;
import java.util.ArrayList;
import java.util.List;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class CurrencyFragment extends Fragment
    implements CurrencyContract.View, CurrencyClickListener {

  private CurrencyContract.Presenter presenter;

  private CurrencyAdapter recyclerAdapter;

  private Unbinder unbinder;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  public CurrencyFragment() {
  }

  public static CurrencyFragment newInstance() {
    return new CurrencyFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    recyclerAdapter = new CurrencyAdapter(new ArrayList<>(0), this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.currency_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.addItemDecoration(new CurrencyDividerItemDecoration(getContext()));
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(recyclerAdapter);

    return rootView;
  }

  @Override public void onStart() {
    super.onStart();
    presenter.start();
  }

  @Override public void showCurrenciesUnavailable() {
    Snackbar.make(recyclerView, getString(R.string.currency_empty), Snackbar.LENGTH_SHORT).show();
  }

  @Override public void showCurrencies(List<Currency> currencies) {
    recyclerAdapter.replaceData(currencies);
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  @Override public void setPresenter(CurrencyContract.Presenter presenter) {
    this.presenter = checkNotNull(presenter);
  }

  @Override public void onClick(Currency currency) {
    presenter.savePreferredCurrency(currency);
    getActivity().setResult(Activity.RESULT_OK);
    getActivity().finish();
  }

  public static class CurrencyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int ITEM = 1;
    private List<Currency> currencies;
    private CurrencyClickListener itemClick;

    CurrencyAdapter(List<Currency> currencies, CurrencyClickListener itemClick) {

      this.currencies = currencies;
      this.itemClick = itemClick;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == ITEM) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.currency_item, parent, false);
        return new CurrencyAdapter.VHItem(view);
      }
      throw new RuntimeException(
          parent.getContext().getString(R.string.exception_no_view_type_found));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder.getItemViewType() == ITEM) {
        CurrencyAdapter.VHItem currency = (CurrencyAdapter.VHItem) holder;
        currency.name.setText(currencies.get(position).getName());
        int id = CurrencyMapper.map(currencies.get(position).getName());
        currency.currencyImage.setBackgroundDrawable(
            ContextCompat.getDrawable(currency.itemView.getContext(), id));
      }
    }

    @Override public int getItemViewType(int position) {
      return ITEM;
    }

    @Override public int getItemCount() {
      return currencies.size();
    }

    private void replaceData(List<Currency> currencies) {
      setList(currencies);
      notifyDataSetChanged();
    }

    private void setList(List<Currency> currencies) {
      this.currencies = checkNotNull(currencies);
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

      View itemView;
      AppCompatTextView name;
      AppCompatImageView currencyImage;

      VHItem(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.name = (AppCompatTextView) itemView.findViewById(R.id.name);
        this.currencyImage = (AppCompatImageView) itemView.findViewById(R.id.img_currency);
        this.itemView.setOnClickListener(this);
      }

      @Override public void onClick(View view) {
        Currency currency = currencies.get(getAdapterPosition());
        itemClick.onClick(currency);
      }
    }
  }

  class CurrencyDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;

    CurrencyDividerItemDecoration(Context context) {
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

        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
