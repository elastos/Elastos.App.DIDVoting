package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.elastos.conf.DidConfiguration;
import org.elastos.conf.ElaServiceConfiguration;
import org.elastos.constants.RetCode;
import org.elastos.constants.VoteSelect;
import org.elastos.constants.VoteTopicType;
import org.elastos.dao.VoteRecordRepository;
import org.elastos.dto.VoteRecord;
import org.elastos.ela.Util;
import org.elastos.pojo.VoteOption;
import org.elastos.pojo.VoteResult;
import org.elastos.pojo.VoteTopicObj;
import org.elastos.util.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class VoteComponent {

    private static Logger logger = LoggerFactory.getLogger(VoteComponent.class);

    @Autowired
    ElaServiceComponent elaServiceComponent;

    @Autowired
    DidVoteService didVoteService;

    @Autowired
    VoteRecordRepository voteRecordRepository;

    @Autowired
    DidConfiguration didConfiguration;

    @Autowired
    ElaServiceConfiguration elaServiceConfiguration;

    private ElaDidService elaDidService = new ElaDidService();
    private List<VoteTopicObj> voteCheckList = new ArrayList<>();

    private Long voteBlockHeight = 0L;

    private void saveToVoteCheckList(VoteRecord voteRecord) {
        VoteTopicObj voteTopicObj = didVoteService.getVoteTopicObj(voteRecord);
        if (null != voteTopicObj) {
            voteCheckList.add(voteTopicObj);
        }
    }

    private void getNewVoteTopicInDb(Long checkHeight) {
        Long blockHeight = voteRecordRepository.getMaxHeight();

        List<VoteRecord> voteTopics = null;
        if (null == checkHeight) {
            voteTopics = voteRecordRepository.findByType(VoteTopicType.VOTE_TOPIC_TYPE_OBJECT);
        } else {
            voteTopics = voteRecordRepository.findByTypeAndHeightIsGreaterThanEqual(VoteTopicType.VOTE_TOPIC_TYPE_OBJECT, checkHeight);
        }

        if (voteTopics.isEmpty()) {
            return;
        }

        for (VoteRecord topic : voteTopics) {
            String topicId = topic.getTopicId();
            List<VoteRecord> voteResults = voteRecordRepository.findByTopicIdAndType(topicId, VoteTopicType.VOTE_TOPIC_TYPE_RESULT);
            if (voteResults.isEmpty()) {
                saveToVoteCheckList(topic);
            }
        }

        voteBlockHeight = blockHeight;
    }

    public void initService() {
        getNewVoteTopicInDb(null);
    }


    boolean count1Vote(Map<Integer, Long> voteCounter, VoteRecord voteRecord, VoteTopicObj obj) {
        Integer maxSelect = obj.getMaxSelections();
        JSONObject vote = null;
        List<VoteOption> options = null;
        try {
            vote = JSON.parseObject(voteRecord.getContent());
            options = vote.getObject("Selections", new TypeReference<List<VoteOption>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err count1Vote exception:" + e.getMessage());
            return false;
        }

        if ((null == options) || options.isEmpty() || (options.size() > maxSelect)) {
            logger.error("Err count1Vote vote key:" + voteRecord.getPropertyKey()
                    + " value:" + voteRecord.getPropertyValue());
            return false;
        }

        String address = this.getAddressFromPublicKey(voteRecord.getPublicKey());
        Double rest = null;
        try {
            rest = elaServiceComponent.getRestOfEla(address);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err count1Vote getRestOfEla exception:" + e.getMessage());
            return false;
        }

        Double limitBalance = obj.getLimitBalance();
        if ((null != limitBalance) && (rest < limitBalance)) {
            logger.error("Err count1Vote limit balance failed.");
            return false;
        }

        Long count = 0L;
        if (null != rest) {
            count = Math.round(rest);
        }
        if (0L == count) {
            count = 1L;
        }

        for (VoteOption option : options) {
            Integer id = option.getOptionID();
            if (null != id) {
                Long c = voteCounter.get(id);
                if (null != c) {
                    c += count;
                } else {
                    return false;
                }
                voteCounter.put(id, c);
            }
        }
        return true;
    }

    private boolean sentResultOnChain(String topicId, List<VoteResult> voteResults) {
        JSONObject opts = new JSONObject(true);
        opts.put("Options", voteResults);
        JSONObject data = new JSONObject(true);
        data.put("Timestamp", System.currentTimeMillis());
        data.put("DID", didConfiguration.getDid());
        data.put("PublicKey", didConfiguration.getPublicKey());
        data.put("RequestedContent", opts);

        String encode;
        String str;
        try {

            str = JSON.toJSONString(data);
            encode = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        String sig = elaDidService.signMessage(didConfiguration.getPrivateKey(), str);

        JSONObject upData = new JSONObject();
        upData.put("DataStr", encode);
        upData.put("Signature", sig);

        String dataJson = upData.toJSONString();
        String address = "did-voting/" + topicId + "/topic_result";

        String ret = didVoteService.save(address, dataJson);
        ServerResponse response = JSON.parseObject(ret, ServerResponse.class);
        if (response.getState() == RetCode.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    private boolean dealVote(VoteTopicObj obj) {
        Map<Integer, Long> voteCounter = new HashMap<>();
        for (Integer opt : obj.getOptionList()) {
            voteCounter.put(opt, 0L);
        }

        List<VoteRecord> voteRecords = voteRecordRepository.findByTopicIdAndTypeAndHeightBetween(
                obj.getTopicId(), VoteTopicType.VOTE_TOPIC_TYPE_VOTE,
                obj.getStartingHeight(), obj.getEndHeight());
        if (voteRecords.isEmpty()) {
            //In case network failure, we judge it is empty or get data failed which cased by network, by block height saved in db.
            Long height = voteRecordRepository.getMaxHeight();
            if (height < obj.getEndHeight()) {
                return false;
            }
        }

        for (VoteRecord record : voteRecords) {
            count1Vote(voteCounter, record, obj);
        }

        //Save all vote info
        List<VoteResult> voteResultList = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : voteCounter.entrySet()) {
            VoteResult vr = new VoteResult();
            vr.setOptionID(entry.getKey());
            vr.setVotes(entry.getValue());
            vr.setResult(VoteSelect.VOTE_UNSELECTED);
            voteResultList.add(vr);
        }

        //Find the selected options by Descending votes
        voteResultList.sort(new Comparator<VoteResult>() {
            @Override
            public int compare(VoteResult o1, VoteResult o2) {
                Long ret = o2.getVotes() - o1.getVotes();
                return ret.intValue();
            }
        });

        Integer max = obj.getMaxSelections();
        Integer rSize = voteResultList.size();
        for (int i = 0; i < max; i++) {
            if ((i < rSize) && (voteResultList.get(i).getVotes() > 0)) {
                voteResultList.get(i).setResult(VoteSelect.VOTE_SELECTED);
            }
        }

        return sentResultOnChain(obj.getTopicId(), voteResultList);
    }

    void checkVoteTask() {
        getNewVoteTopicInDb(voteBlockHeight);

        //Find time to date vote to deal.
        Long height;
        try {
            height = elaServiceComponent.getBlockHeightOfDid();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Iterator<VoteTopicObj> iterator = voteCheckList.iterator();
        while (iterator.hasNext()) {
            VoteTopicObj obj = iterator.next();
            if (obj.getEndHeight() <= height) {
                if (dealVote(obj)) {
                    iterator.remove();
                }
            }
        }
    }

    private String getAddressFromPublicKey(String publicKey) {
        byte[] pub = DatatypeConverter.parseHexBinary(publicKey);
        byte[] rs = Util.CreateSingleSignatureRedeemScript(pub, 1);
        byte[] ph = Util.ToCodeHash(rs, 1);
        return Util.ToAddress(ph);
    }

}
