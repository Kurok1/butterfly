package indi.butterfly.controller;

import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.controller.body.DatasourceBody;
import indi.butterfly.domain.DatasourceConfig;
import indi.butterfly.repository.DatasourceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 数据源配置访问controller
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 */
@RestController
@RequestMapping("/api")
public class DatasourceConfigController extends BaseController {

    private DatasourceConfigRepository datasourceConfigRepository;


    @Autowired
    public void setDatasourceConfigRepository(DatasourceConfigRepository datasourceConfigRepository) {
        this.datasourceConfigRepository = datasourceConfigRepository;
    }

    @PostMapping("/datasource")
    public ResponseMessage<DatasourceConfig> saveOrUpdate(HttpServletRequest request, @RequestBody DatasourceConfig datasourceConfig) {
        if (datasourceConfig.getId() == null || !datasourceConfigRepository.existsById(datasourceConfig.getId())) {
            //检查driverClass是否存在
            try {
                Driver driver = (Driver) Class.forName(datasourceConfig.getDriverClass()).newInstance();
                //注册驱动
                DriverManager.registerDriver(driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                //驱动类不存在
                return MessageFactory.errorResponse("数据库驱动暂不支持");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                //驱动类访问权限不足
                return MessageFactory.errorResponse("数据库驱动无法访问");
            } catch (InstantiationException e) {
                e.printStackTrace();
                //驱动类实例化失败
                return MessageFactory.errorResponse("数据库驱动实例化失败");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return MessageFactory.errorResponse("数据库驱动注册失败");
            }
        }
        if (datasourceConfig.getId() == null)
            datasourceConfig.setCreated(LocalDateTime.now());
        datasourceConfig.setLastUpdated(LocalDateTime.now());
        datasourceConfig.setCreatedBy(getUser(request));
        datasourceConfig.setLastUpdatedBy(getUser(request));
        datasourceConfig = datasourceConfigRepository.save(datasourceConfig);
        return MessageFactory.successResponse("success", datasourceConfig);
    }

    @GetMapping("/datasource/{id}")
    public ResponseMessage<DatasourceConfig> getById(@PathVariable("id") long id) {
        return MessageFactory.successResponse("success", datasourceConfigRepository.findById(id).orElse(null));
    }

    @GetMapping("/datasource/code/{code}")
    public ResponseMessage<DatasourceConfig> getByCode(@PathVariable("code") String code) {
        return MessageFactory.successResponse("success", datasourceConfigRepository.findByCode(code).orElse(null));
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<DatasourceConfig> deleteById(@PathVariable("id") long id) {
        if (!datasourceConfigRepository.existsById(id))
            return MessageFactory.errorResponse("数据源配置不存在");
        else {
            datasourceConfigRepository.deleteById(id);
            return MessageFactory.successResponse("success", null);
        }
    }

    @PostMapping("datasource/testConnection")
    public ResponseMessage<Object> testDatasource(@RequestBody DatasourceBody body) {
        //检查driverClass是否存在
        try {
            Driver driver = (Driver) Class.forName(body.getDriverClass()).newInstance();
            //注册驱动
            DriverManager.registerDriver(driver);
            Connection connection = DriverManager.getConnection(body.getUrl(), body.getUser(), body.getPassword());
            if (connection == null)
                return MessageFactory.errorResponse("数据库异常,连接失败");

            connection.close();
            connection = null;
            return MessageFactory.successResponse("success", null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //驱动类不存在
            return MessageFactory.errorResponse("数据库驱动暂不支持");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //驱动类访问权限不足
            return MessageFactory.errorResponse("数据库驱动无法访问");
        } catch (InstantiationException e) {
            e.printStackTrace();
            //驱动类实例化失败
            return MessageFactory.errorResponse("数据库驱动实例化失败");
        } catch (SQLException e) {
            e.printStackTrace();
            return MessageFactory.errorResponse("数据库异常,连接失败");
        }
    }
}
