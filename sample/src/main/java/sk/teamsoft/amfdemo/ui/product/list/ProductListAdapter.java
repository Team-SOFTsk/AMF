package sk.teamsoft.amfdemo.ui.product.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sk.teamsoft.amfdemo.R;
import sk.teamsoft.amfdemo.data.model.Product;

/**
 * Products list adapter
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();

    @Override public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_product, parent, false));
    }

    @Override public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = productList.get(position);
        holder.setName(product.getName());
        holder.setId(product.getId());
    }

    /**
     * Notifies list about changes
     *
     * @param products new data
     */
    public void updateData(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return productList.size();
    }

    public List<Product> getData() {
        return productList;
    }

    @Nullable public Product getItem(int position) {
        if (position < 0 || position >= productList.size()) return null;
        return productList.get(position);
    }
}