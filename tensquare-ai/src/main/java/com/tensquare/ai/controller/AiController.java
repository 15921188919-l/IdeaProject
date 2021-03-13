package com.tensquare.ai.controller;

import com.tensquare.ai.service.CnnService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 智能分类
 * 传入文本，得到所属分类信息
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private CnnService cnnService;

    /**
     * @param content
     * @return
     */
    @RequestMapping(value = "/textclassify", method = RequestMethod.POST)
    public Map textClassify(@RequestBody Map<String, String> content) {
        return cnnService.textClassify(content.get("content"));
    }
}
