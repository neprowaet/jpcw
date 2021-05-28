package neprowaet.jpcw.data;

import neprowaet.jpcw.data.ConnectionInfo;
import neprowaet.jpcw.data.Data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Handler<T extends Data> {

    void handleData(T dataBlock);

    // автовайринг дома:
    default void handle(Data data) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method handler = this.getClass().getDeclaredMethods()[0];
        Class<?> dataBlock = handler.getParameterTypes()[0];
        Object o = data.getClass().getField(dataBlock.getSimpleName()).get(data);
        handler.invoke(this, o);
    }
}
