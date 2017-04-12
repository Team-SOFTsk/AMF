package sk.teamsoft.amf.data.persister;

import android.text.TextUtils;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dusan Bartos
 */
public class StringCollectionPersister extends StringType {

    private static final StringCollectionPersister INSTANCE = new StringCollectionPersister();
    private static final String DELIMITER = ",";

    public static StringCollectionPersister getSingleton() {
        return INSTANCE;
    }

    private StringCollectionPersister() {
        super(SqlType.STRING, new Class<?>[]{List.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        List<String> myFieldClass = (List<String>) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((String) sqlArg) : null;
    }

    private String getJsonFromMyFieldClass(List<String> myFieldClass) {
        return TextUtils.join(DELIMITER, myFieldClass);
    }

    private List<String> getMyFieldClassFromJson(String json) {
        return Arrays.asList(TextUtils.split(json, DELIMITER));
    }
}
