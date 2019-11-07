package Problem_4;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Collection<T> extends ArrayList<T> {
    public Collection(T... args) {
        super(Arrays.asList(args));
    }

    public Collection() {
        super();
    }

    public boolean any(Predicate<T> predicate) {
        for (T val: this) {
            if (!predicate.test(val)) return false;
        }
        return true;
    }

    public boolean some(Predicate<T> predicate) {
        for (T val: this) {
            if (predicate.test(val)) return true;
        }
        return false;
    }

    public Collection<T> filter(Predicate<T> predicate) {
        Collection<T> newCollection = new Collection<>();
        for (T val: this) {
            if (predicate.test(val)) newCollection.add(val);
        }
        return newCollection;
    }

    public Collection<T> insert(List<T> list, int index) {
        Collection<T> newCollection = new Collection<>();
        for (int i = 0; i < size(); i++) {
            if (i == index) newCollection.addAll(list);
            newCollection.add(this.get(i));
        }
        if (size() == 0 && index == 0) newCollection.addAll(list);
        return newCollection;
    }

    public T findFirst(Predicate<T> predicate) {
        for (T val: this) {
            if (predicate.test(val)) return val;
        }
        return null;
    }

    public Collection<T> splice(int start, int end) {
        Collection<T> newCollection = new Collection<>();
        for (int i = 0; i < size(); i++) {
            if (i < start || i >= end) newCollection.add(this.get(i));
        }
        return newCollection;
    }

    public Collection<T> qsort(Comparator<T> comparator) {
        if (size() < 2) return this;
        Collection<T> newCollection = new Collection<>();
        T pivot = get(0);
        newCollection.addAll(this.filter(val -> comparator.compare(val, pivot) <= 0).qsort(comparator));
        newCollection.add(pivot);
        newCollection.addAll(this.filter(val -> comparator.compare(val, pivot) > 0).qsort(comparator));
        return newCollection;
    }

    public<E> Collection<E> map(Function<T, E> mapFunc) {
        Collection<E> newCollection = new Collection<>();
        for (T val: this) {
            newCollection.add(mapFunc.apply(val));
        }
        return newCollection;
    }

    public T[] array(Class<? extends T> clazz) {
        Object[] array = (Object[]) Array.newInstance(clazz, size());
        for (int i = 0; i < size(); i++) array[i] = get(i);
        return (T[]) array;
    }

    public String toString(Class<? extends T> clazz) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < size(); i++) {
            T val = get(i);
            if (i != 0) result.append(", ");
            result.append(clazz.isArray() ? Arrays.toString((double[]) val) : String.valueOf(val));
        }
        return result.append("]").toString();
    }
}
