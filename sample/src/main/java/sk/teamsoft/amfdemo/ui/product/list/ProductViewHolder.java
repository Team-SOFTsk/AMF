package sk.teamsoft.amfdemo.ui.product.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sk.teamsoft.amfdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View holder for Product
 *
 * @author Dusan Bartos
 */
public class ProductViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.product_list_item_name) TextView nameView;
    @BindView(R.id.product_list_item_mat) TextView idView;

    public ProductViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    /**
     * Sets product name
     *
     * @param name name
     */
    public void setName(String name) {
        nameView.setText(name);
    }

    public void setId(String id) {
        idView.setText(id);
    }
}
