package sk.teamsoft.amfdemo.data.provider;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import sk.teamsoft.amf.data.provider.StorageManager;
import sk.teamsoft.amfdemo.data.model.Product;
import sk.teamsoft.rxlog.RxLog;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 *         Created on 14.04.2017.
 */
@Singleton
public class ProductProvider {

    final BehaviorSubject<List<Product>> productsSubject = BehaviorSubject.create();

    @Inject ProductProvider(StorageManager storageManager) {
        storageManager.observe(Product.class)
                .compose(RxLog.log("ProductProvider:observe:products"))
                .subscribe(productsSubject::onNext, t -> Timber.e(t, "Error observing products"));
    }

    public Observable<List<Product>> observeProducts() {
        //        return productsSubject;
        return Observable.just(Arrays.asList(
                new Product("Product 1", "p1"),
                new Product("Product 2", "p2"),
                new Product("Product 3", "p3"),
                new Product("Product 4", "p4"),
                new Product("Product 5", "p5"),
                new Product("Product 6", "p6")
        ));
    }
}
