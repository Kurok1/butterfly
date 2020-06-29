package indi.butterfly.controller;

import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.domain.User;
import indi.butterfly.repository.UserRepository;
import indi.butterfly.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * //TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 */
@RestController
@RequestMapping("api")
public class UserController extends BaseController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/user")
    public ResponseMessage<User> save(HttpServletRequest request, @RequestBody User user) {
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            return MessageFactory.errorResponse("用户已存在!");
        }

        if (StringUtils.isEmpty(user.getCode())) {
            return MessageFactory.errorResponse("用户编码不能为空");
        }

        if (userRepository.existsByCode(user.getCode())) {
            return MessageFactory.errorResponse("用户已存在!");
        }

        //密码处理
        String password = TextUtil.decode(user.getPassword().getBytes());
        if (StringUtils.isEmpty(password))
            return MessageFactory.errorResponse("密码不能为空");
        password = TextUtil.toPassword(password);
        user.setPassword(password);
        if (user.getId() == null)
            user.setCreated(LocalDateTime.now());
        String createdBy = getUser(request);
        user = userRepository.save(user);
        user.setCreatedBy(createdBy);
        user.setLastUpdated(LocalDateTime.now());
        user.setLastUpdatedBy(createdBy);

        userRepository.save(user);
        return MessageFactory.successResponse("success", user);
    }

    @GetMapping("/user/{id}")
    public User get(@PathVariable("id")Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/user")
    public ResponseMessage<Object> delete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            userRepository.deleteById(id);
        }
        return MessageFactory.successResponse("success", null);
    }

}
