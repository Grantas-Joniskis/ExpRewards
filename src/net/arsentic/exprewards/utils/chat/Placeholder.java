package net.arsentic.exprewards.utils.chat;

import net.arsentic.exprewards.core.Reward;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Placeholder<T> {

    public static final Placeholder<Player> PLAYER_NAME = new Placeholder<>("%player_name%", Player.class, "getName");
    public static final Placeholder<Reward> EXP_LVL = new Placeholder<>("%exp_lvl%", Reward.class, "getLevel");

    private final String variable;
    private final Class<T> valueClass;
    private final String methodName;

    public Placeholder(String variable, Class<T> valueClass, String methodName) {
        this.variable = variable;
        this.valueClass = valueClass;
        this.methodName = methodName;
    }

    public Object getValue(T obj) {
        Object result = null;
        try {
            Method m = obj.getClass().getMethod(this.methodName);
            m.setAccessible(true);
            result = m.invoke(obj);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getVariable() {
        return variable;
    }

    public Class<T> getValueClass() {
        return valueClass;
    }

    public String getMethodName() {
        return methodName;
    }
}