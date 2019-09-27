package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elastos.conf.DidConfiguration;
import org.elastos.conf.ElaServiceConfiguration;
import org.elastos.conf.NodeConfiguration;
import org.elastos.exception.ElastosServiceException;
import org.elastos.pojo.DidProperty;
import org.elastos.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElaServiceComponent {
    private static Logger logger = LoggerFactory.getLogger(ElaServiceComponent.class);
    @Autowired
    NodeConfiguration nodeConfiguration;

    @Autowired
    ElaServiceConfiguration elaServiceConfiguration;

    @Autowired
    DidConfiguration didConfiguration;

    private <T> T getElaChainInfo(String url, Class<T> clazz) throws Exception {
        String response = HttpUtil.get(url, null);
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("HttpUtil.get null");
        } else {
            JSONObject msg = JSON.parseObject(response);
            if (msg.getInteger("Error") == 0) {
                return msg.getObject("Result", clazz);
            } else {
                logger.error("Err url:" + url + " Error:" + msg.getInteger("Error") + " Result:" + msg.get("Result"));
                return null;
            }
        }
    }

    Double getRestOfEla(String address) throws Exception {
        Double obj = getElaChainInfo(elaServiceConfiguration.getElaNodePrefix()
                + nodeConfiguration.getBalanceByAddr() + "/" + address, Double.class);
        return obj;
    }

    Long getBlockHeightOfDid() throws Exception{
        Long obj = getElaChainInfo(elaServiceConfiguration.getDidNodePrefix()
                + nodeConfiguration.getBlockHeight(), Long.class);
        return obj;
    }

    List<DidProperty> getVoteInfo(Long height){
        String response = HttpUtil.get(elaServiceConfiguration.getDidServicePrefix()
                + "/api/1/didexplorer/did/" + didConfiguration.getDid()
                + "/property_like?key=did-voting&blockheightmin="+height, null);
        if (null == response) {
            logger.error("Err: getVoteInfo HttpUtil.get failed");
            throw new RuntimeException("getVoteInfo HttpUtil.get failed");
        }

        JSONObject msg = JSON.parseObject(response);
        if (msg.getIntValue("status") == 200) {
            String result = msg.getString("result");
            if (StringUtils.isBlank(result)) {
                return null;
            } else {
                return JSON.parseArray(result).toJavaList(DidProperty.class);
            }
        } else {
            System.out.println("Err: getVoteInfo failed state:" + msg.get("status") + " result:" + msg.get("result"));
            return null;
        }
    }
}
