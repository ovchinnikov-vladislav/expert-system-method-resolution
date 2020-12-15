package ml.ovcorp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<F, S> {

    private F first;
    private S second;

    public void set(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
