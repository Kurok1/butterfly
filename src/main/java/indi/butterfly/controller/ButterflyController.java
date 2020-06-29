package indi.butterfly.controller;

import indi.butterfly.autoconfigure.ButterflyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * //TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 */
@RestController
@RequestMapping("api")
public class ButterflyController {

    private final ButterflyProperties butterflyProperties;

    @Autowired
    public ButterflyController(ButterflyProperties butterflyProperties) {
        this.butterflyProperties = butterflyProperties;
    }

    @GetMapping("getConfig")
    public ButterflyProperties getConfig() {
        return butterflyProperties;
    }

}
