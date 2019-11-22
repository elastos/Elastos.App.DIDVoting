package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.lang3.StringUtils;
import org.elastos.conf.DidConfiguration;
import org.elastos.conf.ElaServiceConfiguration;
import org.elastos.constants.RetCode;
import org.elastos.constants.VoteTopicType;
import org.elastos.dao.VoteRecordRepository;
import org.elastos.dto.VoteRecord;
import org.elastos.pojo.DidProperty;
import org.elastos.pojo.VoteOption;
import org.elastos.pojo.VoteTopicObj;
import org.elastos.util.HttpUtil;
import org.elastos.util.RetResultB;
import org.elastos.util.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DidVoteService {

    private static Logger logger = LoggerFactory.getLogger(DidVoteService.class);


    @Autowired
    DidConfiguration didConfiguration;

    @Autowired
    ElaServiceConfiguration elaServiceConfiguration;

    @Autowired
    ElaServiceComponent elaServiceComponent;

    @Autowired
    VoteComponent voteComponent;

    @Autowired
    VoteRecordRepository voteRecordRepository;

    private ElaDidService elaDidService = new ElaDidService();

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

        RetResultB<VoteRecord> recordRetResultB = this.checkVote(address, data);
        if (!recordRetResultB.isSuccess()) {
            logger.error("Err checkVote failed.");
            return new ServerResponse().setState(RetCode.ERROR_PARAMETER).setMsg("无效参数").toJsonString();
        }

        VoteRecord record = voteRecordRepository.save(recordRetResultB.getData());
        if (null == record) {
            logger.error("Err checkVote failed.");
            return new ServerResponse().setState(RetCode.ERROR_INTERNAL).setMsg("服务内部异常").toJsonString();
        }

        String rawData = didService.packDidProperty(didPrivateKey, address, data);
        if (null == rawData) {
            logger.error("Err save packDidRawData failed.");
            return new ServerResponse().setState(RetCode.ERROR_INTERNAL).setMsg("打包信息错误").toJsonString();
        }

        String txid = didService.upChainByAgent(elaServiceConfiguration.getDidServicePrefix(), null, null, rawData);
        if (null == txid) {
            logger.error("Err save upChainData failed.");
            System.out.println("Err save upChainData failed.");
            return new ServerResponse().setState(RetCode.ERROR_INTERNAL).setMsg("上链失败").toJsonString();
        }

        Map<String, String> map = new HashMap<>();

        //11.22红包活动
        String packet = getPacketData(record);
        if (null != packet) {
            map.put("packet", packet);

        }

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

    String getPacketData(VoteRecord record) {
        if (record.getType().equals(VoteTopicType.VOTE_TOPIC_TYPE_VOTE)
                && elaServiceConfiguration.getPacketTopicId().equals(record.getTopicId())) {
            String address = voteComponent.getAddressFromPublicKey(record.getPublicKey());
            Double rest = null;
            try {
                rest = elaServiceComponent.getRestOfEla(address);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Err getPacketData getRestOfEla exception:" + e.getMessage());
                return null;
            }

            if (rest < 10.0) {
                logger.error("Err getPacketData rest not enough");
                return null;
            }

            String response = HttpUtil.get(elaServiceConfiguration.getPacketUrl()
                    + "&name=" + record.getDid() + "&address=" + address, null);
            if (null == response) {
                logger.error("Err: getPacketData HttpUtil.get failed");
                return null;
            }

            return response;
        } else {
            return null;
        }
    }

    private VoteRecord verifyCheck(VoteRecord voteRecord, String data) {
        try {
            JSONObject map = JSON.parseObject(data, Feature.OrderedField);
            String encode = map.getString("DataStr");
            String sign = map.getString("Signature");
            if (StringUtils.isAnyBlank(encode, sign)) {
                logger.error("Err verifyCheck DataStr sign failed. data:" + data);
                return null;
            }

            String msg = java.net.URLDecoder.decode(encode, "UTF-8");
            JSONObject obj = JSON.parseObject(msg);
            String publicKey = obj.getString("PublicKey");

            //1. Check signature
            boolean verify = elaDidService.verifyMessage(publicKey, sign, msg);
            if (!verify) {
                logger.error("Err verifyCheck signature verify failed. data:" + data);
                return null;
            }

            //2. Check public key(early than vote time, and has to be on chain)
            String did = obj.getString("DID");

            DidProperty didProperty = elaServiceComponent.getPublicKey(did);
            if ((null == didProperty) || (didProperty.getValue() == null)) {
                logger.error("Err verifyCheck public key verify failed. No public key");
                return null;
            }

            if (!didProperty.getValue().equals(publicKey)) {
                logger.error("Err verifyCheck public key verify failed. wrong public key of did:" + did);
                return null;
            }

            String content = obj.getString("RequestedContent");
            if (StringUtils.isBlank(content)) {
                //Old version being compatible
                content = obj.getString("RequestedConent");
            }

            if (StringUtils.isAnyBlank(did, content)) {
                logger.error("Err verifyCheck content failed. data:" + data);
                return null;
            }
            voteRecord.setContent(content);
            voteRecord.setPublicKey(publicKey);
            voteRecord.setDid(did);
            voteRecord.setDidHeight(didProperty.getHeight());
            return voteRecord;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err verifyCheck exception:" + e.getMessage());
            return null;
        }
    }


    private VoteRecord topicChecker(VoteRecord voteRecord) {
        VoteTopicObj voteTopicObj = this.getVoteTopicObj(voteRecord);
        if (null != voteTopicObj) {
            return voteRecord;
        } else {
            logger.error("Err topicChecker failed.");
            return null;
        }
    }

    private VoteRecord setHeight(VoteRecord voteRecord) {
        Long height;
        try {
            height = elaServiceComponent.getBlockHeightOfDid();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err topicChecker exception:" + e.getMessage());
            return null;
        }

        voteRecord.setHeight(height);
        return voteRecord;
    }

    private VoteRecord voteChecker(VoteRecord voteRecord, String voterDid) {
        //1. did should the same
        if (!voteRecord.getDid().equals(voterDid)) {
            logger.error("Err voteChecker did failed. address did:" + voterDid);
            return null;
        }

        Sort sort = new Sort(Sort.Direction.DESC, "height");
        List<VoteRecord> voteResults = voteRecordRepository.findByTopicIdAndType(voteRecord.getTopicId(), VoteTopicType.VOTE_TOPIC_TYPE_OBJECT, sort);
        if (voteResults.isEmpty()) {
            logger.error("Err voteChecker find topic failed. topicId:" + voteRecord.getTopicId());
            return null;
        }

        //2. DID创建时间要早于投票开始时间
        VoteRecord topicRecord = voteResults.get(0);
        if (voteRecord.getHeight() <= topicRecord.getHeight()) {
            logger.error("Err voteChecker height check failed. topicId:" + voteRecord.getTopicId());
            return null;
        }

        VoteTopicObj voteTopicObj = getVoteTopicObj(topicRecord);
        if (null == voteTopicObj) {
            logger.error("Err voteChecker height check failed. topicId:" + voteRecord.getTopicId());
            return null;
        }

        Map<Integer, Long> voteCounter = new HashMap<>();
        for (Integer opt : voteTopicObj.getOptionList()) {
            voteCounter.put(opt, 0L);
        }

        if (!voteComponent.count1Vote(voteCounter, voteRecord, voteTopicObj)) {
            logger.error("Err voteChecker content check failed. topicId:" + voteRecord.getTopicId());
            return null;
        }

        return voteRecord;
    }

    VoteTopicObj getVoteTopicObj(VoteRecord voteRecord) {
        JSONObject topic;
        Long startHeight = null;
        Long endHeight = null;
        Double limitBalance = null;
        Integer maxSelections = null;
        List<VoteOption> options = null;
        try {
            topic = JSON.parseObject(voteRecord.getContent());
            startHeight = topic.getLong("Starting-height");
            endHeight = topic.getLong("End-height");
            maxSelections = topic.getInteger("Max-Selections");
            limitBalance = topic.getDouble("Limit-balance");
            options = topic.getObject("Options", new TypeReference<List<VoteOption>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((null == startHeight) || (null == endHeight)
                || (null == maxSelections) || (null == options)
                || options.isEmpty()) {
            logger.error("Err getVoteTopicObj key:" + voteRecord.getPropertyKey()
                    + " value:" + voteRecord.getPropertyValue());
            return null;
        }

        VoteTopicObj voteTopicObj = new VoteTopicObj();
        voteTopicObj.setTopicId(voteRecord.getTopicId());
        voteTopicObj.setStartingHeight(startHeight);
        voteTopicObj.setEndHeight(endHeight);
        voteTopicObj.setLimitBalance(limitBalance);
        voteTopicObj.setMaxSelections(maxSelections);
        List<Integer> ops = new ArrayList<>();
        for (VoteOption op : options) {
            ops.add(op.getOptionID());
        }
        voteTopicObj.setOptionList(ops);

        return voteTopicObj;
    }


    private VoteRecord resultChecker(VoteRecord voteRecord) {
        return voteRecord;
    }

    RetResultB<VoteRecord> checkVote(String address, String data) {
        String[] list = address.split("/");
        if (list.length == 0) {
            logger.info("Vote address format error.");
            return RetResultB.retErr("Vote address format error.");
        }

        List<String> voteKeyParseList = Arrays.asList(list);
        VoteRecord voteRecord = new VoteRecord();
        String topicId = voteKeyParseList.get(1);
        voteRecord.setTopicId(topicId);
        voteRecord.setPropertyKey(address);
        voteRecord.setPropertyValue(data);
        voteRecord.setServiceDid(didConfiguration.getDid());
        voteRecord = this.setHeight(voteRecord);
        if (null == voteRecord) {
            return RetResultB.retErr("Vote network error.");
        }

        voteRecord = this.verifyCheck(voteRecord, data);
        if (null == voteRecord) {
            return RetResultB.retErr("Vote verify error");
        }

        if (voteKeyParseList.contains("topic_object")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_OBJECT);
            voteRecord = this.topicChecker(voteRecord);
        } else if (voteKeyParseList.contains("vote_object")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_VOTE);
            voteRecord = this.voteChecker(voteRecord, voteKeyParseList.get(3));
        } else if (voteKeyParseList.contains("topic_result")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_RESULT);
            voteRecord = this.resultChecker(voteRecord);
        } else {
            logger.info("Vote address not support. address:" + address);
            return RetResultB.retErr("Vote address not support. address:" + address);
        }

        if (null == voteRecord) {
            return RetResultB.retErr("Vote check failed.");
        }

        return RetResultB.retOk(voteRecord);
    }

}
