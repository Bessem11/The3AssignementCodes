import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TFTheOne {

    private Object value;

    // ALESSIO This is not optimal. I see that you pass java methods around, but
    // why? Would have been more simpler and correct to define an interface or use a
    // function as defined by java functional interface?
    private MasterMild mastermild = new MasterMild();

    public TFTheOne(Object value) {
        this.value = value;

    }

    public TFTheOne bind(Method func)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        this.value = func.invoke(mastermild, this.value);

        return this;
    }

    public void printme() {

        List<List<String>> Outputs = (List<List<String>>) this.value;

        for (int i = 0; i < Outputs.size(); i++) {
            for (int j = 0; j < Outputs.get(i).size(); j++) {
                System.out.println(Outputs.get(i).get(j));
            }
        }

    }

}
