package sk.teamsoft.amfdemo.ui.product.list;

import android.support.v7.widget.RecyclerView;

import sk.teamsoft.amf.framework.AmfView;

/**
 * @author Dusan Bartos
 */
public interface ProductListView extends AmfView {
    void setProductListAdapter(RecyclerView.Adapter adapter);
}
