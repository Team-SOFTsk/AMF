package sk.teamsoft.amfdemo.ui.product.list;

import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import sk.teamsoft.amfdemo.R;
import sk.teamsoft.amf.framework.AmfActivity;
import sk.teamsoft.amfannotations.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_product_list)
public class ProductListActivity extends AmfActivity {

    @BindView(R.id.fab) FloatingActionButton fab;

    private ProductListFragment fragment;

    @Override protected void onResume() {
        super.onResume();
        fragment = (ProductListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_product_list);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        if (fragment != null) {
            handled = fragment.onMenuItemClick(item.getItemId());
        }
        return handled || super.onOptionsItemSelected(item);
    }
}
