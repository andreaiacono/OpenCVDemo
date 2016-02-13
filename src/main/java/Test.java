import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 23/01/16
 * Time: 14.58
 */
public class Test {

    public static int solution(int[] A) {
        // write your code in Java SE 8

        int N = A.length;
        int left=-1;
        do {
            left++;
        } while (left<N-1 && A[left] <= A[left+1]);

        int right=N;
        do {
            right--;
        } while (right>0 && A[right] >= A[right-1]);

        int max = 0;
        int min = 100000000;

        for (int j=left; j<=right; j++) {
            if (A[j] > max) max = A[j];
            if (A[j] < min) min = A[j];
        }

        int deltaLeft = left;
        for (int j=0; j<left; j++) {
            if (A[j] > min) {
                deltaLeft = j;
                break;
            }
        }

        int deltaRight = right;
        for (int j=N-1; j>right; j--) {
            if (A[j] < max) {
                deltaRight = j+1;
                break;
            }
        }

        return deltaRight - deltaLeft;
    }
    public static void main(String[] args) {
        System.err.println(solution(new int[]{1,1,2}));
    }
}
