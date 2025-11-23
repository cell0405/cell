import java.util.ArrayList;
import java.util.List;

public class StringExtractor {
    // 定义三种状态的枚举
    private enum State {
        PARSE_STATE,      // 解析状态
        ADD_CHAR_STATE,   // 添加字符状态
        ESCAPE_STATE      // 转义字符状态
    }
    
    private State currentState;          // 当前状态
    private StringBuilder currentString; // 当前正在构建的字符串
    private List<String> extractedStrings; // 提取到的所有字符串
    
    /**
     * 构造函数，初始化状态机
     */
    public StringExtractor() {
        currentState = State.PARSE_STATE;  // 初始状态为解析状态
        currentString = new StringBuilder();
        extractedStrings = new ArrayList<>();
    }
    
    /**
     * 处理输入的字符流
     * @param input 输入的字符串
     */
    public void processInput(String input) {
        for (char c : input.toCharArray()) {
            processCharacter(c);
        }
    }
    
    /**
     * 处理单个字符，根据当前状态和字符类型进行状态转换和相应操作
     * @param c 要处理的字符
     */
    private void processCharacter(char c) {
        switch (currentState) {
            case PARSE_STATE:
                // 解析状态下遇到双引号，进入添加字符状态，开始构建新字符串
                if (c == '"') {
                    currentState = State.ADD_CHAR_STATE;
                    currentString.setLength(0); // 重置当前字符串
                }
                // 解析状态下遇到其他字符，不做处理
                break;
                
            case ADD_CHAR_STATE:
                if (c == '"') {
                    // 遇到双引号，完成当前字符串提取，返回解析状态
                    extractedStrings.add(currentString.toString());
                    currentState = State.PARSE_STATE;
                } else if (c == '\\') {
                    // 遇到反斜杠，进入转义字符状态
                    currentState = State.ESCAPE_STATE;
                } else {
                    // 遇到其他字符，添加到当前字符串，保持添加字符状态
                    currentString.append(c);
                }
                break;
                
            case ESCAPE_STATE:
                // 转义字符状态下，遇到任意字符都直接添加到当前字符串，然后返回添加字符状态
                currentString.append(c);
                currentState = State.ADD_CHAR_STATE;
                break;
        }
    }
    
    /**
     * 获取提取到的所有字符串
     * @return 提取到的字符串列表
     */
    public List<String> getExtractedStrings() {
        return new ArrayList<>(extractedStrings);
    }
    

    public static void main(String[] args) {
        StringExtractor extractor = new StringExtractor();
        
        // 测试用例
        String testInput = "这是一段测试代码，包含\"普通字符串\"和\"带\\转义\\n的字符串\"，以及未闭合的\"字符串";
        extractor.processInput(testInput);
        
        // 输出提取结果
        System.out.println("提取到的字符串:");
        for (String str : extractor.getExtractedStrings()) {
            System.out.println(str);
        }
    }
}