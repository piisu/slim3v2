package org.slim3plus.model.mr;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

public class MapperContext implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class Top implements Serializable {
        private static final long serialVersionUID = 1L;

        @Data
        public static class Entry {
            private Object key;
            private Long value;
        }

        private boolean asc;

        private int n;

        private List<Object> keys;

        private List<Long> values;

        private List<Entry> toEntries() {
            List<Entry> entries = new ArrayList<>();

            for (int i = 0; i < keys.size(); i++) {
                Entry e = new Entry();
                e.key = keys.get(i);
                e.value = values.get(i);
                entries.add(e);
            }

            return entries;

        }

        public Top() {
        }

        public Top(int n, boolean isAsc) {
            this.asc = isAsc;
            keys = new ArrayList<>();
            values = new ArrayList<>();
            this.n = n;
        }

        public void add(Object key, long value) {
            values.add(value);
            if (asc) {
                Collections.sort(values);
            } else {
                Collections.sort(values, Collections.reverseOrder());
            }
            int index = values.indexOf(value);
            keys.add(index, key);
            if (n < keys.size()) {
                keys.remove(n);
                values.remove(n);
            }
        }

        public List<Object> getKeys() {
            return keys;
        }

        public List<Long> getValues() {
            return values;
        }

        public boolean isAsc() {
            return asc;
        }

        public LinkedHashMap<Object, Long> asMap() {
            LinkedHashMap<Object, Long> map = new LinkedHashMap<Object, Long>();
            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i), values.get(i));
            }

            return map;
        }
    }

    public class Counter implements Serializable {
        private static final long serialVersionUID = 1L;

        Map<Object, Long> counter = new HashMap<Object, Long>();

        public long increment(Object key) {
            return addCount(key, 1);
        }

        public long addCount(Object key, long addValue) {
            long value = addValue;
            if (counter.containsKey(key)) {
                value = counter.get(key) + addValue;
            }
            counter.put(key, value);
            return value;
        }

        public long get(Object key) {
            if (counter.containsKey(key)) {
                return counter.get(key);
            }
            return 0L;
        }

        @Override
        public String toString() {
            return String.valueOf(counter);
        }

        public Set<Map.Entry<Object, Long>> entrySet() {
            return counter.entrySet();
        }

        public Map<Object, Long> getCounter() {
            return counter;
        }
    }

    private Map<String, Counter> counters = new HashMap<>();

    private Map<String, Top> topMap = new HashMap<>();

    private static String DEFAULT_COUNTER_NAME = "__default__";

    public Map<String, List<Top.Entry>> getTopEntries() {
        Map<String, List<Top.Entry>> map = new LinkedHashMap<>();
        for (Map.Entry<String, Top> e : topMap.entrySet()) {
            map.put(e.getKey(), e.getValue().toEntries());
        }

        return map;
    }

    public Map<String, Counter> getCounters() {
        return counters;
    }

    public Counter getCounter() {
        return getCounter(DEFAULT_COUNTER_NAME);
    }

    public Counter getCounter(String name) {
        Counter counter = counters.get(name);
        if (counter == null) {
            counter = new Counter();
            counters.put(name, counter);
        }
        return counter;
    }

    public Top getTop(String name) {
        return topMap.get(name);
    }

    public Top getOrCreateTop(String name, int n, boolean isAsc) {
        Top top = topMap.get(name);
        if (top == null) {
            top = new Top(n, isAsc);
            topMap.put(name, top);
        }
        return top;
    }

    @Override
    public String toString() {
        return String.valueOf(counters);
    }

    public void merge(MapperContext context) {
        for (Map.Entry<String, Counter> entry : context.counters.entrySet()) {
            final Counter counter = this.counters.get(entry.getKey());
            if (counter == null) {
                counters.put(entry.getKey(), entry.getValue());
            } else {
                for (Map.Entry<Object, Long> count : entry.getValue().counter.entrySet()) {
                    counter.addCount(count.getKey(), count.getValue());
                }
            }
        }

        for (Map.Entry<String, Top> entry : context.topMap.entrySet()) {
            final Top current = topMap.get(entry.getKey());
            if (current == null) {
                topMap.put(entry.getKey(), entry.getValue());
            } else {
                Top topN = entry.getValue();
                for (int i = 0; i < topN.keys.size(); i++) {
                    current.add(topN.getKeys().get(i), topN.getValues().get(i));
                }
            }
        }
    }
}
