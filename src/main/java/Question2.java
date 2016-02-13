import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Assuming that any of the iterators is empty. Assuming that all the iterators dequeue
 * ordered data items. If any iterator contains the data item [MAX_VALUE, MAX_VALUE, ...] the algorithms doesn't
 * work because it's used as a reserved data item.
 *
 *
 */
public class Question2 {

    private final int K = 3;
    private final int M = 3;
    private final int N = 4;
    private final DataItem maxDataItem = new DataItem(IntStream.range(0, K).map(n->Integer.MAX_VALUE).toArray());

    /**
     * the comparator of the data items. It has O(K) time complexity.
     */
    public Comparator<DataItem> dataItemComparator = (dataItem, otherDataItem) -> {
        int index = 0;
        while (index < K-1 && dataItem.getKey(index) == otherDataItem.getKey(index)) {
            index++;
        }
        return (dataItem.getKey(index) < otherDataItem.getKey(index)) ? -1 :
                ((dataItem.getKey(index) == otherDataItem.getKey(index)) ? 0 : 1);
    };

    /**
     * merges the iterators with an ascending order of their data items.
     * This method has O((M*K)+(M*K*N)) -> O((M*K)(1+N)) -> O(M*K*N) time complexity
     * and O(K*M) space complexity (excluding the result).
     * @param iterators the iterators to merge
     * @return an ascending sorted iterator of data items
     */
    public Iterator<DataItem> mergeIterators(Iterator<DataItem>[] iterators) {

        // the max size of the result is the number of data items for each iterator by the number of the iterators
        DataItem[] result = new DataItem[N*M];
        int resultFillerIndex = 0;
        boolean isEmptyIterators;

        // the last data item dequeued from all the iterators
        DataItem[] currentDataItems = new DataItem[M];

        // finds the min data item among all the first data items dequeued from the M iterators
        // this loop has O(M*K) time complexity
        DataItem minDataItem = maxDataItem;
        int minIndex = -1;
        for (int m=0; m<M; m++) {

            Iterator<DataItem> iterator = iterators[m];
            if (iterator.hasNext()) {

                // gets the first data item of this iterator
                DataItem firstDataItem = iterator.next();
                currentDataItems[m] = firstDataItem;

                // checks it with min data item
                if (dataItemComparator.compare(firstDataItem, minDataItem) < 0) {
                    minDataItem = firstDataItem;
                    minIndex = m;
                }
            }
        }

        // we've found the first data item for the result (the smallest among all the iterators)
        result[resultFillerIndex++] = minDataItem;

        // now we loop over the M iterators to get - for every step - the smallest data item
        // this loop has O(M*K*N) complexity
        do {
            if (iterators[minIndex].hasNext()) {
                // gets the new data item from the iterator that gave us the min data item on the preceding step
                currentDataItems[minIndex] = iterators[minIndex].next();
            }
            else {
                // if that iterator is empty, we put a special data item for marking it
                currentDataItems[minIndex] = maxDataItem;
            }

            isEmptyIterators = true;
            minDataItem = maxDataItem;

            // now finds the min among the current data items of the M iterators
            for (int m=0; m<M; m++) {

                DataItem currentDataItem = currentDataItems[m];

                // skips the empty iterators
                if (currentDataItem.equals(maxDataItem)) {
                    continue;
                }

                isEmptyIterators = false;
                if (dataItemComparator.compare(minDataItem, currentDataItem) > 0) {
                    minDataItem = currentDataItem;
                    minIndex = m;
                }
            }

            // if a data item is found, it's put into the result
            if (!minDataItem.equals(maxDataItem)) {
                result[resultFillerIndex++] = minDataItem;
            }
        }
        while (!isEmptyIterators);

        // transforms the array into an iterator
        return Arrays.asList(result).iterator();
    }

    /**
     * this class represent the data item of an iterator.
     */
    class DataItem {

        private int[] keys;

        public DataItem(int[] keys) {
            this.keys = keys;
        }

        public int getKey(int index) {
            assert(index < K);
            return keys[index];
        }

        @Override
        public String toString() {
            return Arrays.toString(keys);
        }

        @Override
        public boolean equals(Object o) {
            // not checking on types!
            DataItem dataItem = (DataItem) o;
            return Arrays.equals(keys, dataItem.keys);
        }
    }


    /// the tests should obviously be located in another file
    @org.junit.Test
    public void testMergeIterators() {

        DataItem[] m1 = new DataItem[] { new DataItem(new int[] {1,2,3}), new DataItem(new int[] {1,2,3}), new DataItem(new int[] {1,2,5}), new DataItem(new int[] {1,5,1})};
        DataItem[] m2 = new DataItem[] { new DataItem(new int[] {1,2,3}), new DataItem(new int[] {1,3,1}), new DataItem(new int[] {1,3,5}), new DataItem(new int[] {3,1,1})};
        DataItem[] m3 = new DataItem[] { new DataItem(new int[] {1,2,9}), new DataItem(new int[] {1,3,2}), new DataItem(new int[] {1,5,5}), new DataItem(new int[] {2,5,1})};
        Iterator<DataItem>[] iterators = new Iterator[] { Arrays.asList(m1).iterator(), Arrays.asList(m2).iterator(), Arrays.asList(m3).iterator()};
        Question2 q2 = new Question2();
        Iterator<DataItem> result = q2.mergeIterators(iterators);
        assertEquals(result.next(), new DataItem(new int[] {1,2,3}));
        assertEquals(result.next(), new DataItem(new int[] {1,2,3}));
        assertEquals(result.next(), new DataItem(new int[] {1,2,3}));
        assertEquals(result.next(), new DataItem(new int[] {1,2,5}));
        assertEquals(result.next(), new DataItem(new int[] {1,2,9}));
        assertEquals(result.next(), new DataItem(new int[] {1,3,1}));
        assertEquals(result.next(), new DataItem(new int[] {1,3,2}));
        assertEquals(result.next(), new DataItem(new int[] {1,3,5}));
        assertEquals(result.next(), new DataItem(new int[] {1,5,1}));
        assertEquals(result.next(), new DataItem(new int[] {1,5,5}));
        assertEquals(result.next(), new DataItem(new int[] {2,5,1}));
        assertEquals(result.next(), new DataItem(new int[] {3,1,1}));
    }

    @org.junit.Test
    public void testKeyComparator() {
        Comparator<DataItem> dataItemComparator = new Question2().dataItemComparator;

        DataItem q1 = new DataItem(new int[]{1, 2, 3});
        DataItem q2 = new DataItem(new int[]{2, 2, 3});
        assertEquals(-1, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{1, 2, 3});
        q2 = new DataItem(new int[]{1, 3, 3});
        assertEquals(-1, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{1, 2, 3});
        q2 = new DataItem(new int[]{1, 2, 4});
        assertEquals(-1, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{1, 2, 3});
        q2 = new DataItem(new int[]{1, 2, 3});
        assertEquals(0, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{2, 2, 3});
        q2 = new DataItem(new int[]{1, 2, 3});
        assertEquals(1, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{1, 3, 3});
        q2 = new DataItem(new int[]{1, 2, 3});
        assertEquals(1, dataItemComparator.compare(q1, q2));

        q1 = new DataItem(new int[]{1, 2, 4});
        q2 = new DataItem(new int[]{1, 2, 3});
        assertEquals(1, dataItemComparator.compare(q1, q2));
    }



}
