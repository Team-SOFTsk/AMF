package sk.teamsoft.amfdemo.data.storage;

import android.content.Context;

import com.j256.ormlite.field.DataPersisterManager;

import java.util.Arrays;
import java.util.List;

import sk.teamsoft.amf.data.persister.CalendarPersister;
import sk.teamsoft.amf.data.persister.StringCollectionPersister;
import sk.teamsoft.amf.data.storage.AmfDbHelper;
import sk.teamsoft.amfdemo.data.model.Product;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 *         Created on 03.04.2017.
 */

public class DbHelper extends AmfDbHelper {
    private static final String DBNAME = "amf.demo";
    private static final int DBVERSION = 1;

    public DbHelper(Context context) {
        super(context, DBNAME, DBVERSION);
    }

    @Override protected void registerPersisters() {
        Timber.v("registerPersisters");

        //edit persisters if necessary
        DataPersisterManager.registerDataPersisters(
                CalendarPersister.getSingleton(),
                StringCollectionPersister.getSingleton()
        );
    }

    @Override protected List<Class> getTables() {
        Timber.v("getTables");
        //add tables to returned collection
        return Arrays.asList(Product.class);
    }

    @Override protected void migrate(int from, int to) {
        Timber.v("migrate from=%d to=%d", from, to);

        /*int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    //TODO add migration when upgrading DB schema
                    break;

                default:
                    throw new IllegalStateException("onUpgrade() with unknown newVersion" + newVersion);
            }
            upgradeTo++;
        }*/
    }
}
