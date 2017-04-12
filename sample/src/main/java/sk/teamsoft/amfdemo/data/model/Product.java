package sk.teamsoft.amfdemo.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Dusan Bartos
 *         Created on 03.04.2017.
 */
@DatabaseTable(tableName = "products")
public class Product {

    @DatabaseField String name;
    @DatabaseField String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
