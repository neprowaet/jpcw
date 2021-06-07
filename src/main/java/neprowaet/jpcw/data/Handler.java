package neprowaet.jpcw.data;

import java.lang.reflect.Method;

public interface Handler<T extends Data> {

    void handleData(T data);

    // автовайринг дома:
    default void handle(Data data) {
        try {
            for (Method m : this.getClass().getDeclaredMethods()) {
                if (!m.getName().equals("handleData")) continue;
                if (m.getParameterTypes()[0].getSimpleName().equals("Data")) continue;
                Class<?> dataBlock = m.getParameterTypes()[0];
                Object o = data.getClass().getField(dataBlock.getSimpleName()).get(data);
                m.invoke(this, o);

            }
        } catch (Exception e) {
            System.err.println("Handling data error: " + this.getClass().getName());
        }
    }

}
