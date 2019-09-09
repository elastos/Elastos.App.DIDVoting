/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elastos.annotation.Auth;
import org.elastos.service.DidVoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1/didvoting")
public class AssociationController {
    private static Logger logger = LoggerFactory.getLogger(AssociationController.class);

    @Autowired
    private DidVoteService didVoteService;


    @RequestMapping(value = "save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String certificate(@RequestAttribute String reqBody) {
        logger.info("save:"+ reqBody);
        JSONObject map = JSON.parseObject(reqBody);
        String address = map.getString("address");
        String data = map.getString("data");
        return didVoteService.save(address, data);
    }

    @RequestMapping(value = "echo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String echo(@RequestAttribute String reqBody) {
        logger.info("StarController echo data:" + reqBody);
        return reqBody;
    }
}
