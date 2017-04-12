package sk.teamsoft.amf.data.storage;

import com.j256.ormlite.dao.Dao;

import sk.teamsoft.amf.data.model.DBEntity;

/**
 * Abstraction of DBHelper to get rid of Android framework specific classes for testing
 *
 * @author Dusan Bartos
 */
public interface IDatabase {
    <T extends DBEntity> Dao<T, Object> getCachedDao(Class<T> clazz);
}
