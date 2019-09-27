// Java‚ÅTryParse‚ðŽÀ‘•‚·‚é
// https://qiita.com/choco14t/items/7b7e82ae46a98dc24da9

import java.util.Objects;

public class ExInteger {

    public static boolean TryParse(String str, Out<Integer> result) {
        Objects.requireNonNull(str, "str");
        Objects.requireNonNull(result, "result");

        int value = 0;
        boolean isNegative = false;

        for (char c : str.toCharArray()) {
            if (value == 0 && c == '-') {
                isNegative = true;
                continue;
            }

            if (c < '0' || c > '9') {
                result.set(0);
                return false;
            }

            value *= 10;
            value += c - '0';

        }

        value = isNegative ? -value : value;
        result.set(value);
        return true;
    }
}
