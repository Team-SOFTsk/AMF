package sk.teamsoft.amfdemo.ui.product.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import sk.teamsoft.amf.framework.AmfFragment;
import sk.teamsoft.amfannotations.AutoSubcomponent;
import sk.teamsoft.amfannotations.Layout;
import sk.teamsoft.amfdemo.R;
import sk.teamsoft.amfdemo.dagger.module.ProductListModule;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_product_list)
@AutoSubcomponent(
        module = ProductListModule.class,
        presenter = ProductListPresenter.class,
        name = "ProductList"
)
public class ProductListFragment extends AmfFragment<ProductListView, ProductListPresenter>
        implements ProductListView {

    @BindView(R.id.product_list) RecyclerView list;

    @Inject ProductListPresenter productListPresenter;

    @NonNull @Override public ProductListPresenter createPresenter() {
        return productListPresenter;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override public void setProductListAdapter(RecyclerView.Adapter adapter) {
        list.setAdapter(adapter);
    }
}
