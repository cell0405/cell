import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

// 状态枚举
enum State {
    DETECTING,      // 检测状态
    HEATING,        // 加热状态
    CONSTANT_TEMP,  // 恒温状态
    WATER_SHORTAGE, // 缺水状态
    SLEEPING,       // 休眠状态
    FAULT           // 故障状态
}

// 事件枚举
enum Event {
    TEMP_BELOW_20,          // 温度低于20℃
    TEMP_BETWEEN_20_100,    // 温度在20-100℃之间
    TEMP_REACH_100,         // 温度达到100℃
    WATER_AVAILABLE,        // 有水
    NO_WATER,               // 无水
    TIME_2300,              // 晚上11点
    TIME_0700,              // 早上7点
    TANK_BROKEN,            // 水箱烧坏
    WATER_ADDED             // 加水
}

// 传感器数据类
class SensorData {
    private int temperature; // 温度
    private boolean hasWater; // 是否有水

    public SensorData(int temperature, boolean hasWater) {
        this.temperature = temperature;
        this.hasWater = hasWater;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean hasWater() {
        return hasWater;
    }
}

// 恒温水箱状态机
class WaterTankStateMachine {
    private State currentState;
    private Timer timer;

    public WaterTankStateMachine() {
        // 初始状态为检测状态
        this.currentState = State.DETECTING;
        initTimer();
        System.out.println("设备启动，进入检测状态");
    }

    // 初始化定时器，处理时间事件
    private void initTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now();
                // 检查是否到达晚上11点
                if (now.getHour() == 23 && now.getMinute() == 0) {
                    handleEvent(Event.TIME_2300);
                }
                // 检查是否到达早上7点
                if (now.getHour() == 7 && now.getMinute() == 0) {
                    handleEvent(Event.TIME_0700);
                }
            }
        }, 0, 60000); // 每分钟检查一次
    }

    // 处理事件
    public void handleEvent(Event event) {
        State nextState = currentState;
        
        switch (currentState) {
            case DETECTING:
                nextState = handleDetectingState(event);
                break;
            case HEATING:
                nextState = handleHeatingState(event);
                break;
            case CONSTANT_TEMP:
                nextState = handleConstantTempState(event);
                break;
            case WATER_SHORTAGE:
                nextState = handleWaterShortageState(event);
                break;
            case SLEEPING:
                nextState = handleSleepingState(event);
                break;
            case FAULT:
                nextState = handleFaultState(event);
                break;
        }

        // 如果状态发生变化，执行状态转换
        if (nextState != currentState) {
            exitCurrentState();
            currentState = nextState;
            enterNewState();
        }
    }

    // 处理检测状态的事件
    private State handleDetectingState(Event event) {
        switch (event) {
            case TEMP_BELOW_20:
                return Event.WATER_AVAILABLE == event ? State.HEATING : State.WATER_SHORTAGE;
            case TEMP_BETWEEN_20_100:
                return State.CONSTANT_TEMP;
            case TIME_2300:
                return State.SLEEPING;
            default:
                return currentState;
        }
    }

    // 处理加热状态的事件
    private State handleHeatingState(Event event) {
        switch (event) {
            case TEMP_REACH_100:
                return State.CONSTANT_TEMP;
            case TEMP_BELOW_20:
                return State.HEATING; // 继续加热
            case TANK_BROKEN:
                return State.FAULT;
            case TIME_2300:
                return State.SLEEPING;
            default:
                return currentState;
        }
    }

    // 处理恒温状态的事件
    private State handleConstantTempState(Event event) {
        switch (event) {
            case TEMP_BELOW_20:
                return Event.WATER_AVAILABLE == event ? State.HEATING : State.WATER_SHORTAGE;
            case TIME_2300:
                return State.SLEEPING;
            default:
                return currentState;
        }
    }

    // 处理缺水状态的事件
    private State handleWaterShortageState(Event event) {
        switch (event) {
            case WATER_ADDED:
                return State.DETECTING;
            case TIME_2300:
                return State.SLEEPING;
            default:
                return currentState;
        }
    }

    // 处理休眠状态的事件
    private State handleSleepingState(Event event) {
        if (event == Event.TIME_0700) {
            return State.DETECTING;
        }
        return currentState;
    }

    // 处理故障状态的事件
    private State handleFaultState(Event event) {
        // 这里简化处理，实际应用中可能需要维修完成的事件
        return currentState;
    }

    // 退出当前状态时执行的动作
    private void exitCurrentState() {
        switch (currentState) {
            case HEATING:
                System.out.println("退出加热状态：断开继电器电源");
                break;
            // 其他状态退出动作根据需要添加
            default:
                System.out.println("退出" + currentState + "状态");
        }
    }

    // 进入新状态时执行的动作
    private void enterNewState() {
        switch (currentState) {
            case HEATING:
                System.out.println("进入加热状态：打开继电器电源");
                break;
            case CONSTANT_TEMP:
                System.out.println("进入恒温状态：保持温度");
                break;
            case WATER_SHORTAGE:
                System.out.println("进入缺水状态：提示缺水");
                break;
            case FAULT:
                System.out.println("进入故障状态：进行维修");
                break;
            // 其他状态进入动作
            default:
                System.out.println("进入" + currentState + "状态");
        }
    }

    // 处理传感器数据
    public void processSensorData(SensorData data) {
        if (data.getTemperature() < 20) {
            handleEvent(Event.TEMP_BELOW_20);
            handleEvent(data.hasWater() ? Event.WATER_AVAILABLE : Event.NO_WATER);
        } else if (data.getTemperature() >= 20 && data.getTemperature() < 100) {
            handleEvent(Event.TEMP_BETWEEN_20_100);
        } else if (data.getTemperature() >= 100) {
            handleEvent(Event.TEMP_REACH_100);
        }
    }

    // 停止定时器
    public void stop() {
        timer.cancel();
    }

    public State getCurrentState() {
        return currentState;
    }
}

// 测试类
public class WaterTankTest {
    public static void main(String[] args) throws InterruptedException {
        WaterTankStateMachine machine = new WaterTankStateMachine();
        
        // 模拟各种场景
        System.out.println("\n场景1：温度15℃，有水");
        machine.processSensorData(new SensorData(15, true));
        
        System.out.println("\n场景2：温度升到80℃");
        machine.processSensorData(new SensorData(80, true));
        
        System.out.println("\n场景3：温度升到100℃");
        machine.processSensorData(new SensorData(100, true));
        
        System.out.println("\n场景4：温度降到18℃");
        machine.processSensorData(new SensorData(18, true));
        
        System.out.println("\n场景5：温度15℃，无水");
        machine.processSensorData(new SensorData(15, false));
        
        System.out.println("\n场景6：加水");
        machine.handleEvent(Event.WATER_ADDED);
        
        machine.stop();
    }
}