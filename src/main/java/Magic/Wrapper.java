package Magic;

import com.sun.istack.internal.Nullable;
import java.io.Serializable;
import java.util.ArrayList;

public class Wrapper implements Serializable {
    private int stage; // Текущая стадия выполнения протокола, нужна для корректной отправки данных
    private ArrayList<Object> data; // Массив данных, необходимых на текущей стадии для выполнения протокола
    private final Object object; //
// Геттеры
    public int getStage() {
        return stage;
    }
    public ArrayList<Object> getData() {
        return data;
    }
    public Object getObject() { return object; }
// Сеттер
    public Wrapper(int stage, @Nullable ArrayList<Object> data, @Nullable Object object ){
        this.stage=stage;
        this.data=data;
        this.object=object;
    }



}
