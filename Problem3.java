    public class Problem3 {
        public static void main(String[] args) {
            int limit = 20000;
            int count = 0;
            int number = 2;
            
            System.out.println("Prime numbers between 1 and 20000:");
            
            while (number <= limit) {
                if (isPrime(number)) {
                    System.out.print(number + "\t");
                    count++;
                    if (count % 5 == 0) {
                        System.out.println();
                    }
                }
                number++;
            }
        }
        public static boolean isPrime(int num) {
            if (num <= 1) {
                return false;
            }
            for (int i = 2; i * i <= num; i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }