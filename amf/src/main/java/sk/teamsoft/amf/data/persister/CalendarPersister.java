package sk.teamsoft.amf.data.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongType;

import java.util.Calendar;

/**
 * @author Dusan Bartos
 */
public class CalendarPersister extends LongType {

    private static final CalendarPersister INSTANCE = new CalendarPersister();

    public static CalendarPersister getSingleton() {
        return INSTANCE;
    }

    public CalendarPersister() {
        super(SqlType.STRING, new Class<?>[]{Calendar.class});
    }

    @Override public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        Calendar myFieldClass = (Calendar) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((Long) sqlArg) : null;
    }

    private Long getJsonFromMyFieldClass(Calendar myFieldClass) {
        return myFieldClass.getTimeInMillis();
    }

    private Calendar getMyFieldClassFromJson(Long json) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(json);
        return cal;
    }
}
