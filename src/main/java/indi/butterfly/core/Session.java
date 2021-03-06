package indi.butterfly.core;

import indi.butterfly.domain.User;

import java.util.Map;

/**
 * 用户会话
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.10
 * @since 1.0.0
 */
public class Session {

    private String user;

    private String token;

    private String device;//来源设备

    private Map<String, Object> params;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public static Session of(String user, String token, Map<String, Object> params) {
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setParams(params);
        return session;
    }
}
