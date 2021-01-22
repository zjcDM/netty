/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author: z00425519
 * @since: 2021-01-22
 **/
public class Console {
    public static void main(String[] args) {
        Comparator<Map.Entry<String, Set<String>>> storeComparator = new Comparator<Map.Entry<String, Set<String>>>() {

            @Override
            public int compare(Map.Entry<String, Set<String>> o1, Map.Entry<String, Set<String>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        };

        Comparator<String> keyComparator = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        Comparator<String> valueComparator = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return Long.compare(Long.parseLong(o2), Long.parseLong(o1));
            }
        };

        Comparator<Map.Entry<String, PriorityQueue<String>>> finalComparator = new Comparator<Map.Entry<String, PriorityQueue<String>>>() {

            @Override
            public int compare(Map.Entry<String, PriorityQueue<String>> o1, Map.Entry<String, PriorityQueue<String>> o2) {
                return Long.compare(Long.parseLong(o1.getKey()), Long.parseLong(o2.getKey()));
            }
        };

        TimestampProcess<String, String> timestampProcess = new TimestampProcess<>("D:\\code\\index-build-test\\src\\main\\resources\\input.txt", "output.txt", storeComparator, keyComparator, valueComparator, finalComparator);
        System.out.println(timestampProcess.process());
    }
}
