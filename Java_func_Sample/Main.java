import java.util.*;

public class Program {
    static void Main(string[] args) {
        ref.takingMethod(param -> bodyExpression);
    }

    interface StringFunction {
        int func(String param);
    }

    public void takingMethod(StringFunction sf) {
       int i = sf.func("my string");
       // do whatever ...
    }

    ref.takingMethod(new StringFunction() {
        public int func(String param) {
            // body
        }
    });
}
