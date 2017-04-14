package sk.teamsoft.amfdemo.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import sk.teamsoft.amf.data.model.DBEntity;

/**
 * @author Dusan Bartos
 *         Created on 03.04.2017.
 */
@DatabaseTable(tableName = "products")
public class Product implements DBEntity {

    @DatabaseField String name;
    @DatabaseField String id;

    Product() {}

    public Product(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
