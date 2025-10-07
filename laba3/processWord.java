import java.util.*;

public class processWord {
    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static String[] normalizeAndSplit(String text) {
        String[] raw = text.toLowerCase().split("\\P{L}+");
        List<String> words = new ArrayList<>();
        for (String w : raw) {
            if (!w.isEmpty()) words.add(w);
        }
        return words.toArray(new String[0]);
    }

    public static List<String[]> findReversePairs(String text) {
        String[] words = normalizeAndSplit(text);
        Set<String> set = new HashSet<>(Arrays.asList(words));

        List<String[]> pairs = new ArrayList<>();
        for (String w : set) {
            String rev = reverse(w);
            if (!w.equals(rev) && set.contains(rev) && w.compareTo(rev) < 0) {
                pairs.add(new String[]{w, rev});
            }
        }
        pairs.sort(Comparator.comparing((String[] p) -> p[0]).thenComparing(p -> p[1]));
        return pairs;
    }
}
