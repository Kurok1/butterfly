package indi.butterfly.core;

import indi.butterfly.domain.User;

import java.util.Map;

/**
 * 用户会话
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.10
 */
public class Session {

    private String user;

    private String token;

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

    public static Session of(String user, String token, Map<String, Object> params) {
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setParams(params);
        return session;
    }
}
