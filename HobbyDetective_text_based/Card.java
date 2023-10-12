//superclass for weapon, estate and character cards
public class Card<T> {
    T type;
    String name;

    Card(T type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPrintable() {
        return type + " " + name;
    }

    public T getType() {
        return type;
    }
}