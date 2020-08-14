package indi.butterfly.controller;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.controller.body.LoginSuccessBody;
import indi.butterfly.controller.body.UserAuthBody;
import indi.butterfly.core.AuthService;
import indi.butterfly.domain.User;
import indi.butterfly.repository.UserRepository;
import indi.butterfly.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * 登录控制器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.25
 * @since 1.0.0
 */
@RestController
public class LoginController {

    private final UserRepository userRepository;

    private final AuthService authService;

    @Autowired
    public LoginController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }


    /**
     * 登录操作
     * @param request http请求
     * @param authBody 登录请求报文,包括 用户名和密码
     * @return
     */
    @PostMapping("/api/auth/login")
    public ResponseMessage<LoginSuccessBody> login(HttpServletRequest request, @RequestBody UserAuthBody authBody) {

        String password = TextUtil.decrypt(authBody.getPassword());//密码解密处理
        Optional<User> optionalUser = userRepository.getByCodeAndPassword(authBody.getUserCode(), password);
        if (!optionalUser.isPresent()) {
            return MessageFactory.errorResponse("用户名或密码不正确");
        }

        User user = optionalUser.get();
        //获取token
        String token = authService.login(user.getCode());

        return MessageFactory.successResponse("登录成功", LoginSuccessBody.of(user, token));
    }

    @PostMapping("/api/auth/logout/{userCode}")
    public Message logout(HttpServletRequest request, @PathVariable("userCode") String userCode) {
        //退出登录, 清空缓存
        return authService.logout(userCode);
    }

}
