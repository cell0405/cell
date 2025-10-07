import java.util.Scanner;

public class Problem2 {
    public static void main(String[] args) {
        int[] array1 = {1, -2, 3, 5, -1};
        int[] array2 = {1, -2, 3, -8, 5, 1};
        int[] array3 = {1, -2, 3, -2, 5, 1};

        System.out.println("测试用例");
        System.out.println("[1, -2, 3, 5, -1] max subarray sum: " + findMaxSum(array1));
        System.out.println("[1, -2, 3, -8, 5, 1] max subarray sum: " + findMaxSum(array2));
        System.out.println("[1, -2, 3, -2, 5, 1] max subarray sum: " + findMaxSum(array3));
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n请输入一组整数，空格分隔：");
        String input = scanner.nextLine();
        
        String[] strNumbers = input.split("\\s+");
        int[] customArray = new int[strNumbers.length];
        
        for (int i = 0; i < strNumbers.length; i++) {
            customArray[i] = Integer.parseInt(strNumbers[i]);
        }
        
        int result = findMaxSum(customArray);
        System.out.print("输入的数组: [");
        for (int i = 0; i < customArray.length; i++) {
            System.out.print(customArray[i]);
            if (i < customArray.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
        System.out.println("最大子数组和: " + result);
        
        scanner.close();
    }
    
    public static int findMaxSum(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];

        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }
}