/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author: z00425519
 * @since: 2021-01-22
 **/
public class TimestampProcess<K, V> {
    private final String input;

    private final String output;

    private final Comparator<? super Map.Entry<K, Set<V>>> storeComparator;

    private final Comparator<? super V> valueComparator;

    private final Comparator<? super K> keyComparator;

    Comparator<? super Map.Entry<V, PriorityQueue<K>>> finalComparator;

    private final Map<K, Set<V>> store = new HashMap<>(128);

    public TimestampProcess(String input, String output,
                            Comparator<? super Map.Entry<K, Set<V>>> storeComparator,
                            Comparator<? super K> keyComparator,
                            Comparator<? super V> valueComparator,
                            Comparator<? super Map.Entry<V, PriorityQueue<K>>> finalComparator) {
        this.input = input;
        this.output = output;
        this.storeComparator = storeComparator;
        this.valueComparator = valueComparator;
        this.finalComparator = finalComparator;
        this.keyComparator = keyComparator;
    }

    private boolean processInput() {
        try {
            Files.lines(Paths.get(input)).forEach(line -> {
                    String[] infos = line.split(",");
                    if (infos.length == 2) {
                        Set<V> logs = store.computeIfAbsent((K) infos[1], key-> new HashSet<>());
                        logs.add((V) infos[0]);
                    }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean sort() {
        List<Map.Entry<K, Set<V>>> sortedStore = new ArrayList<>(store.entrySet());
        sortedStore.sort(storeComparator);
        PriorityQueue<V> sorter = new PriorityQueue<>(valueComparator);
        Map<V, PriorityQueue<K>> result = new HashMap<>();
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output))))) {
            for (Map.Entry<K, Set<V>> entry: sortedStore) {
                K key = entry.getKey();
                Set<V> values = entry.getValue();
                sorter.addAll(values);
                PriorityQueue<K> vPriorityQueue = result.computeIfAbsent(sorter.poll(), k -> new PriorityQueue<>(keyComparator));
                vPriorityQueue.add(key);
                sorter.clear();
            }

            List<Map.Entry<V, PriorityQueue<K>>> resultList = new ArrayList<>(result.entrySet());
            resultList.sort(finalComparator);
            for (Map.Entry<V, PriorityQueue<K>> entry: resultList) {
                V timestamp = entry.getKey();
                for (K log: entry.getValue()) {
                    writer.write(timestamp + "," + log);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
//            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean process() {
        return processInput() && sort();
    }
}
