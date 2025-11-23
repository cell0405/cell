// 状态接口
public interface State {
    void entry(Context context);
    void doAction(Context context);
    void handleInput(Context context, char input);
    String getStateName();
}

public class Context {
    // 预设密码
    private static final String PASSWORD = "1234";
    
    // 状态实例
    private State readyState;
    private State readInputState;
    private State passwordVerifyState;
    private State lockedState;
    private State functionSelectState;
    
    // 当前状态
    private State currentState;
    
    // 共享数据
    private int errorCount;
    private StringBuilder inputBuffer;
    private int lockTimeRemaining; // 锁定剩余时间（秒）
    
    public Context() {
        // 初始化所有状态
        readyState = new ReadyState();
        readInputState = new ReadInputState();
        passwordVerifyState = new PasswordVerifyState();
        lockedState = new LockedState();
        functionSelectState = new FunctionSelectState();
        
        // 初始状态为就绪状态
        currentState = readyState;
        inputBuffer = new StringBuilder();
    }
    
    // 状态转换
    public void setState(State state) {
        System.out.println("从 " + currentState.getStateName() + " 转换到 " + state.getStateName());
        currentState = state;
        currentState.entry(this);
    }
    
    // 处理输入
    public void handleInput(char input) {
        currentState.handleInput(this, input);
    }
    
    // 执行当前状态的动作
    public void doAction() {
        currentState.doAction(this);
    }
    
    // getter和setter方法
    public State getReadyState() {
        return readyState;
    }
    
    public State getReadInputState() {
        return readInputState;
    }
    
    public State getPasswordVerifyState() {
        return passwordVerifyState;
    }
    
    public State getLockedState() {
        return lockedState;
    }
    
    public State getFunctionSelectState() {
        return functionSelectState;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public StringBuilder getInputBuffer() {
        return inputBuffer;
    }
    
    public void clearInputBuffer() {
        inputBuffer.setLength(0);
    }
    
    public String getPASSWORD() {
        return PASSWORD;
    }
    
    public int getLockTimeRemaining() {
        return lockTimeRemaining;
    }
    
    public void setLockTimeRemaining(int lockTimeRemaining) {
        this.lockTimeRemaining = lockTimeRemaining;
    }
    
    public State getCurrentState() {
        return currentState;
    }
}

// 就绪状态
public class ReadyState implements State {
    @Override
    public void entry(Context context) {
        System.out.println("进入就绪状态");
        // 初始化错误次数为0
        context.setErrorCount(0);
    }
    
    @Override
    public void doAction(Context context) {
        // 就绪状态没有持续动作
    }
    
    @Override
    public void handleInput(Context context, char input) {
        // 有键盘输入时，转换到读取键入字符状态
        context.setState(context.getReadInputState());
        context.handleInput(input);
    }
    
    @Override
    public String getStateName() {
        return "就绪状态";
    }
}

// 读取键入字符状态
public class ReadInputState implements State {
    @Override
    public void entry(Context context) {
        System.out.println("进入读取键入字符状态");
        // 初始化输入字符数为0
        context.clearInputBuffer();
    }
    
    @Override
    public void doAction(Context context) {
    }
    
    @Override
    public void handleInput(Context context, char input) {
        if (Character.isDigit(input)) {
            context.getInputBuffer().append(input);
            System.out.println("已输入字符: " + context.getInputBuffer().toString());
            // 当输入字符数为4时，转换到密码验证状态
            if (context.getInputBuffer().length() == 4) {
                context.setState(context.getPasswordVerifyState());
            }
        }
    }
    
    @Override
    public String getStateName() {
        return "读取键入字符状态";
    }
}

// 密码验证状态
public class PasswordVerifyState implements State {
    @Override
    public void entry(Context context) {
        System.out.println("进入密码验证状态");
        // 将输入的4位字符串与预设密码进行比对
        String inputPassword = context.getInputBuffer().toString();
        String correctPassword = context.getPASSWORD();
        
        if (inputPassword.equals(correctPassword)) {
            System.out.println("密码正确");
            // 密码比对正确时，转换到系统功能选择状态
            context.setState(context.getFunctionSelectState());
        } else {
            System.out.println("密码错误");
            // 若比对错误错误次数 + 1
            context.setErrorCount(context.getErrorCount() + 1);
            System.out.println("错误次数: " + context.getErrorCount());
            
            if (context.getErrorCount() >= 3) {
                // 密码比对错误且当前错误次数 = 3 时，转换到锁定状态
                context.setState(context.getLockedState());
            } else {
                // 密码比对错误且当前错误次数 < 3 时，转换到读取键入字符状态
                context.setState(context.getReadInputState());
            }
        }
    }
    
    @Override
    public void doAction(Context context) {
    }
    
    @Override
    public void handleInput(Context context, char input) {
    }
    
    @Override
    public String getStateName() {
        return "密码验证状态";
    }
}

// 锁定状态
public class LockedState implements State {
    private Thread lockTimer;
    
    @Override
    public void entry(Context context) {
        System.out.println("进入锁定状态");
        // 启动120秒倒计时计时器
        context.setLockTimeRemaining(120);
        
        // 创建并启动计时器线程
        lockTimer = new Thread(() -> {
            try {
                while (context.getLockTimeRemaining() > 0) {
                    Thread.sleep(1000); // 每秒减少1
                    context.setLockTimeRemaining(context.getLockTimeRemaining() - 1);
                    if (context.getLockTimeRemaining() % 10 == 0 || context.getLockTimeRemaining() <= 5) {
                        System.out.println("锁定中，剩余 " + context.getLockTimeRemaining() + " 秒");
                    }
                }
                // 计时器到达120秒时，转换到就绪状态
                context.setState(context.getReadyState());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        lockTimer.start();
    }
    
    @Override
    public void doAction(Context context) {
    }
    
    @Override
    public void handleInput(Context context, char input) {
        // 锁定状态下屏蔽所有输入
        System.out.println("系统已锁定，不接受任何输入");
    }
    
    @Override
    public String getStateName() {
        return "锁定状态";
    }
}

// 系统功能选择状态
public class FunctionSelectState implements State {
    @Override
    public void entry(Context context) {
        System.out.println("进入系统功能选择状态");
        // 显示功能菜单
        System.out.println("功能菜单");
        System.out.println("1. 查看信息");
        System.out.println("2. 修改设置");
        System.out.println("3. 退出");
        System.out.println("请输入功能编号选择:");
    }
    
    @Override
    public void doAction(Context context) {
    }
    
    @Override
    public void handleInput(Context context, char input) {
        // 处理功能选择
        switch (input) {
            case '1':
                System.out.println("执行查看信息功能");
                break;
            case '2':
                System.out.println("执行修改设置功能");
                break;
            case '3':
                System.out.println("退出功能，返回就绪状态");
                context.setState(context.getReadyState());
                return;
            default:
                System.out.println("无效的功能选择");
        }
        entry(context);
    }
    
    @Override
    public String getStateName() {
        return "系统功能选择状态";
    }
}