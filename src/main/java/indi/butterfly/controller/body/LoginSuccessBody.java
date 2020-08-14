package indi.butterfly.controller.body;

import indi.butterfly.domain.User;

import java.time.LocalDateTime;

/**
 * 登录成功返回消息
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.25
 * @since 1.0.0
 */
public class LoginSuccessBody {

    private User user;

    private String token;

    private LocalDateTime loginTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public static LoginSuccessBody of (User user, String token) {
        LoginSuccessBody body = new LoginSuccessBody();
        body.setUser(user);
        body.setToken(token);
        body.setLoginTime(LocalDateTime.now());
        return body;
    }
}
