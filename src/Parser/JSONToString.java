package Parser;

import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class JSONToString {
    public String toString() {
        JSONObject object = new JSONObject();
        Collection<Field> fields = new Collection<>();
        Class clazz = getClass();
        do {
            fields.addAll(new Collection<>(clazz.getDeclaredFields()));
        } while ((clazz = clazz.getSuperclass()) != null);

        fields = fields.reverse();

        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    Object result = field.get(this);
                    if (!(result instanceof Number) && !(result instanceof Collection) && !(result instanceof JSONToString) && !(result instanceof Map)) {
                        result = String.valueOf(result);
                    }
                    if (result instanceof Map) {
                        JSONObject _result = new JSONObject();
                        ((JSONObject) _result).putAll((Map) result);
                        result = _result;
                    }
                    object.put(field.getName(), result);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        //System.out.println(getClass().getName() + object.keySet());
        return getClass().getSimpleName() + object.toJSONString();
    }
}