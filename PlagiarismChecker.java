import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class PlagiarismChecker {


    //最长公共子序列
    private static double calculateLongestCommonSubsequence(String originalFile, String plagiarizedFile) throws FileNotFoundException {
        String text1 = readFile(originalFile);
        String text2 = readFile(plagiarizedFile);
        int[] dp = new int[text2.length() + 1];

        for (int i = 1; i <= text1.length(); i++) {
            // 存储前一行的dp[j]的值以供后续使用
            int prev = dp[0];
            // 初始化当前行的dp[0]
            dp[0] = 0;

            for (int j = 1; j <= text2.length(); j++) {
                // 存储当前行的dp[j]的值以供后续使用
                int temp = dp[j];
                // 如果当前位置的字符匹配
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    // 更新dp[j]为左上角的值加1
                    dp[j] = prev + 1;
                } else {
                    // 否则，取前一行或前一列的较大值
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }
                // 更新prev为当前行的dp[j]的值，供下次迭代使用
                prev = temp;
            }
        }

        // 返回最长公共子序列长度除以两个字符串长度的最大值
        return (double) dp[text2.length()] / Math.max(text1.length(), text2.length());
    }




    //编辑距离
    private static double calculateEditDistance(String originalFile, String plagiarizedFile) throws FileNotFoundException {
        String text1 = readFile(originalFile);
        String text2 = readFile(plagiarizedFile);

        int[] dp = new int[text2.length() + 1];
        for (int j = 0; j <= text2.length(); j++) {
            dp[j] = j;
        }

        for (int i = 1; i <= text1.length(); i++) {
            int prev = dp[0];
            dp[0] = i;
            for (int j = 1; j <= text2.length(); j++) {
                int temp = dp[j];
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j - 1]));
                }
                prev = temp;
            }
        }

        return 1 - (double) dp[text2.length()] / Math.max(text1.length(), text2.length());
    }




    //将文件的数据读取到字符串
    private static String readFile(String filePath) throws FileNotFoundException {
        StringBuilder text = new StringBuilder();
        Scanner scanner = new Scanner(new File(filePath));

        while (scanner.hasNextLine()) {
            text.append(scanner.nextLine()).append(System.lineSeparator());
        }

        scanner.close();
        return text.toString();
    }




    private static void writeResult(String outputFile, double similarity, String originalFile, String plagiarizedFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true))) {
            writer.printf("Original File(原文件): ------------------%s%n", originalFile);
            writer.printf("Plagiarized File(抄袭文件): ------------------%s%n", plagiarizedFile);
            writer.printf("Similarity(相似度): ------------------%.2f%n", similarity);
            writer.printf("Date(检测日期): ------------------%s%n", getCurrentDate());
            writer.println();
            writer.println();
        }
    }

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("无效参数输入，正确命令为：java -jar 【PlagiarismChecker.jar的绝对路径】 original.txt plagiarized.txt output.txt");
            return;
        }

        String originalFile = args[0];
        String plagiarizedFile = args[1];
        String outputFile = args[2];

        try {
            double similarity = (calculateLongestCommonSubsequence(originalFile, plagiarizedFile)+calculateEditDistance(originalFile, plagiarizedFile))/2;
            writeResult(outputFile, similarity, originalFile, plagiarizedFile);
            System.out.println("Similarity(相似度): " + similarity);
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

}






