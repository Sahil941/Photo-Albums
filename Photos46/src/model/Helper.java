package model;

import java.util.List;
import java.util.ListIterator;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiPredicate;

/**
 * This class helps Collection operations.
 *
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class Helper {
    /**
     * @param tCollection Input Collection
     * @param predicate Predicate
     * @param <T> Type Parameter
     * @return True if predicate is met at least by one in the Collection, false otherwise.
     */
    public static <T> boolean findOne(Collection<T> tCollection, Predicate<T> predicate) {
        return tCollection.stream().anyMatch(predicate);
    }

    /**
     * @param tCollection Input Collection
     * @param u Parameter
     * @param biPredicate BiPredicate
     * @param <T> Type Parameter
     * @param <U> Type Parameter
     * @return List of elements in the Collection that meet criteria.
     */
    public static <T,U> List<T> filter(Collection<T> tCollection, U u, BiPredicate<T,U> biPredicate) {
        return tCollection.stream().filter(t -> biPredicate.test(t, u)).collect(Collectors.toList());
    }
    
    public static <T,U> List<U> map(Collection<T> tCollection, Function<T,U> f) {
        return tCollection.stream().map(f).collect(Collectors.toList());
    }

    /**
     * @param list Input list
     * @param t Parameter
     * @param <T> Type parameter
     * @return True if inserted into list
     */
    public static <T extends Comparable<T>> boolean addOrGetList(List<T> list, T t) {
        ListIterator<T> itr = list.listIterator();

        while (true) {
            if (!itr.hasNext()) {
                itr.add(t);
                
                return true;
            }
            
            T listElement = itr.next();
            
            if (listElement.compareTo(t) == 0) {
                return false;
            }
            
            if (listElement.compareTo(t) > 0) {
                itr.previous();
                itr.add(t);
                
                return true;
            }
        }
    }

    /**
     * @param list Input list
     * @param k Parameter
     * @param biPredicate BiPredicate
     * @param <T> Type parameter
     * @param <K> Type parameter
     * @return Element deleted from list
     */
    public static <T extends Comparable<T>, K> T delete(List<T> list, K k, BiPredicate<T,K> biPredicate) {
        ListIterator<T> itr = list.listIterator();

        while (itr.hasNext()) {
            T t = itr.next();
            
            if (biPredicate.test(t, k)) {
                itr.remove();
                
                return t;
            }
        }

        return null;
    }

    /**
     * @param list Input list
     * @param k Parameter
     * @param biPredicate BiPredicate
     * @param <T> Type parameter
     * @param <K> Type parameter
     * @return Element that meets criteria
     */
    public static <T extends Comparable<T>, K> T get(List<T> list, K k, BiPredicate<T,K> biPredicate) {
        return list.stream().filter(t -> biPredicate.test(t, k)).findFirst().orElse(null);
    }

    /**
     * @param source Input list
     * @param condition Input list condition
     * @param biPredicate BiPredicate
     * @param <T> Type parameter
     * @return True if input list condition contains matches compared to source list
     */
    public static <T extends Comparable<T>> boolean search(List<T> source, List<T> condition, BiPredicate<T,T> biPredicate) {
        boolean isAllMatch = true;

        for (T t : condition) {
            boolean isMatch = source.stream().anyMatch(s -> biPredicate.test(s, t));

            if (!isMatch) {
                isAllMatch = false;
                break;
            }
        }

        return isAllMatch;
    }
}