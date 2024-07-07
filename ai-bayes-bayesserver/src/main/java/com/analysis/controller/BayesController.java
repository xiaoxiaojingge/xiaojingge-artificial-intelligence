package com.analysis.controller;

import com.analysis.util.BayesUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * 贝叶斯控制器
 *
 * @author lijing
 * @date 2024-07-06
 */
@RestController
@RequestMapping("/bayes")
public class BayesController {

    @GetMapping("/test")
    public String test(String fileName) throws XMLStreamException, IOException {
        BayesUtil.saveBayes(fileName);
        return "success";
    }
}
