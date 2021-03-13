package com.tensquare.qa.client.impl;

import com.tensquare.qa.client.LabelClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

/**
 * LabelClient的失败处理类
 */
@Component
public class LabelClientImpl implements LabelClient{
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.REMOTE_ERROR,"熔断器开启了...");
    }
}
