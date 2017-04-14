package sk.teamsoft.amfdemo.ui.product.list;

import android.app.Application;
import android.content.res.Resources;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import sk.teamsoft.amf.framework.AmfPresenter;
import sk.teamsoft.amf.framework.IMenuClickablePresenter;
import sk.teamsoft.amfdemo.data.provider.ProductProvider;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class ProductListPresenter extends AmfPresenter<ProductListView> implements IMenuClickablePresenter {

    @Inject Application application;
    @Inject Resources resources;
    @Inject Scheduler mainScheduler;
    @Inject ProductProvider productProvider;

    private ProductListAdapter adapter = new ProductListAdapter();

    @Inject ProductListPresenter() {}

    @Override public void attachView(ProductListView view) {
        super.attachView(view);

        view.setProductListAdapter(adapter);
        add(productProvider.observeProducts()
                .doOnNext(x -> Timber.d("Products updated: %s", x))
                .observeOn(mainScheduler)
                .subscribe(adapter::updateData, baseErrorHandler("productList:observeProducts")));
    }

    @Override public boolean onMenuItemClick(int id) {
        Timber.v("onMenuItemClick %d", id);
        switch (id) {
            default:
                return false;
        }
    }
}
