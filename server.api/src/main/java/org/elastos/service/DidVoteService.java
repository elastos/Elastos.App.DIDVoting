package org.elastos.service;

import org.apache.commons.lang3.StringUtils;
import org.elastos.conf.DidConfiguration;
import org.elastos.conf.ElaServiceConfiguration;
import org.elastos.constants.RetCode;
import org.elastos.util.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DidVoteService {

    private static Logger logger = LoggerFactory.getLogger(DidVoteService.class);

    @Autowired
    DidConfiguration didConfiguration;

    @Autowired
    ElaServiceConfiguration elaServiceConfiguration;

    private ElaDidService didService = new ElaDidService();

    public void initService() {
    }

    public String save(String address, String data) {
        if (StringUtils.isAnyBlank(address, data)) {
            logger.error("save parameter has null");
            return new ServerResponse().setState(RetCode.ERROR_PARAMETER).setMsg("传入参数异常").toJsonString();
        }

        String didPrivateKey = didConfiguration.getPrivateKey();
        String did = didConfiguration.getDid();

        String rawData = didService.packDidProperty(didPrivateKey, address, data);
        if (null == rawData) {
            logger.error("Err save packDidRawData failed.");
            return new ServerResponse().setState(RetCode.ERROR_INTERNAL).setMsg("打包信息错误").toJsonString();
        }

        String txid = didService.upChainByAgent(elaServiceConfiguration.getBlockAgentPrefix(), null, null, rawData);
        if (null == txid) {
            logger.error("Err save upChainData failed.");
            System.out.println("Err save upChainData failed.");
            return new ServerResponse().setState(RetCode.ERROR_INTERNAL).setMsg("上链失败").toJsonString();
        }

        Map<String, String> map = new HashMap<>();
//        //https://idchain.elastos.org/did/igcBAAKG28NDdTfyWDtpH33wevJrKuHay1/property_history/SNH48%E5%86%AF%E8%96%AA%E6%9C%B5
//        String didExplorerUrl = elaServiceConfiguration.getDidExplorerUrl() + "/did/" + did + "/property_history/";
//        try {
//            didExplorerUrl += java.net.URLEncoder.encode(address, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            //https://idchain.elastos.org/properties_list/igcBAAKG28NDdTfyWDtpH33wevJrKuHay1
//            didExplorerUrl = elaServiceConfiguration.getDidExplorerUrl() + "/properties_list/" + did;
//        }
//
//        map.put("did_explorer_url", didExplorerUrl);
        map.put("txid", txid);

        return new ServerResponse().setState(RetCode.SUCCESS).setData(map).toJsonString();
    }
}
