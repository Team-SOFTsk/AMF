package sk.teamsoft.amf.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.teamsoft.amf.data.model.DBEntity;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public abstract class AmfDbHelper extends OrmLiteSqliteOpenHelper implements IDatabase {

    private final Map<Class, Dao<?, Object>> daoMap = new HashMap<>();

    protected AmfDbHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    @Override public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            registerPersisters();

            for (Class table: getTables()) {
                Timber.d("Init %s table", table.getSimpleName());
                TableUtils.createTable(connectionSource, table);
            }
        } catch (java.sql.SQLException e) {
            Timber.e(e, "Error creating DB");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        migrate(oldVersion, newVersion);
    }

    @SuppressWarnings("unchecked")
    @Override public <T extends DBEntity> Dao<T, Object> getCachedDao(Class<T> clazz) {
        if (!daoMap.containsKey(clazz)) {
            try {
                final Dao<T, Object> dao = getDao(clazz);
                daoMap.put(clazz, dao);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return (Dao<T, Object>) daoMap.get(clazz);
    }

    protected abstract void registerPersisters();

    protected abstract List<Class> getTables();

    protected abstract void migrate(int from, int to);
}
