package indi.butterfly.controller.body;

import org.springframework.util.StringUtils;

/**
 * 用户登录请求body,只有用户名和密码1
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.10
 * @since 1.0.0
 */
public class UserAuthBody {

    private String userCode;

    private String password;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNotAuth() {
        return StringUtils.isEmpty(userCode) || StringUtils.isEmpty(password);
    }
}
