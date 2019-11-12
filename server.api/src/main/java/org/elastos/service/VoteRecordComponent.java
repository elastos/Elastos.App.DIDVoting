package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.lang3.StringUtils;
import org.elastos.conf.DidConfiguration;
import org.elastos.conf.ElaServiceConfiguration;
import org.elastos.constants.VoteTopicType;
import org.elastos.dao.VoteRecordRepository;
import org.elastos.dto.VoteRecord;
import org.elastos.pojo.DidProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class VoteRecordComponent {

    private static Logger logger = LoggerFactory.getLogger(VoteRecordComponent.class);

    private Long blockHeight = 0L;

    @Autowired
    ElaServiceComponent elaServiceComponent;

    @Autowired
    VoteRecordRepository voteRecordRepository;

    @Autowired
    DidConfiguration didConfiguration;

    @Autowired
    ElaServiceConfiguration elaServiceConfiguration;

    private ElaDidService elaDidService = new ElaDidService();

    private void getBlockHeight() {
        blockHeight = voteRecordRepository.getMaxHeight();
        if (null == blockHeight) {
            blockHeight = 0L;
        }
    }

    private VoteRecord parseVoteData(VoteRecord voteRecord, String data) {
        try {
            JSONObject map = JSON.parseObject(data, Feature.OrderedField);
            String encode = map.getString("DataStr");
            String sign = map.getString("Signature");
            if (StringUtils.isAnyBlank(encode, sign)) {
                logger.error("Err parseVoteData DataStr sign failed. data:" + data);
                return null;
            }
            String msg = java.net.URLDecoder.decode(encode, "UTF-8");
            JSONObject obj = JSON.parseObject(msg);
            String publicKey = obj.getString("PublicKey");

            boolean verify = elaDidService.verifyMessage(publicKey, sign, msg);
            if (!verify) {
                logger.error("Err parseVoteData verify failed. data:" + data);
                return null;
            }

            String did = obj.getString("DID");
            String content = obj.getString("RequestedContent");
            if (StringUtils.isBlank(content)) {
                //Old version being compatible
                content = obj.getString("RequestedConent");
            }

            if (StringUtils.isAnyBlank(did, content)) {
                logger.error("Err parseVoteData topic failed. data:" + data);
                return null;
            }
            voteRecord.setContent(content);
            voteRecord.setPublicKey(publicKey);
            voteRecord.setDid(did);
            return voteRecord;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveVoteInfo(DidProperty vote) {
        List<VoteRecord> vs = voteRecordRepository.findByPropertyKeyAndPropertyValue(vote.getKey(), vote.getValue());
        if (!vs.isEmpty()) {
            //There is already a same record in db, no need save again.
            return;
        }

        String voteKey = vote.getKey();
        String[] list = voteKey.split("/");
        if (list.length == 0) {
            return;
        }

        List<String> voteKeyParseList = Arrays.asList(list);
        VoteRecord voteRecord = new VoteRecord();
        if (voteKeyParseList.contains("topic_object")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_OBJECT);
        } else if (voteKeyParseList.contains("vote_object")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_VOTE);
        } else if (voteKeyParseList.contains("topic_result")) {
            voteRecord.setType(VoteTopicType.VOTE_TOPIC_TYPE_RESULT);
        } else {
            logger.info("saveVoteInfo can not parse:" + voteKey);
            return;
        }

        voteRecord = parseVoteData(voteRecord, vote.getValue());
        if (null == voteRecord) {
            return;
        }

        String topicId = voteKeyParseList.get(1);
        voteRecord.setTopicId(topicId);
        voteRecord.setHeight(vote.getHeight());
        voteRecord.setPropertyKey(vote.getKey());
        voteRecord.setPropertyValue(vote.getValue());
        voteRecord.setServiceDid(didConfiguration.getDid());

        voteRecordRepository.save(voteRecord);
    }

    void recordTask() {
        this.getBlockHeight();

        List<DidProperty> voteLists = null;
        voteLists = elaServiceComponent.getVoteInfo(blockHeight);
        if (null == voteLists) {
            return;
        }

        voteLists.sort(new Comparator<DidProperty>() {
            @Override
            public int compare(DidProperty o1, DidProperty o2) {
                Long ret = o1.getHeight() - o2.getHeight();
                return ret.intValue();
            }
        });

        for (DidProperty vote : voteLists) {
            saveVoteInfo(vote);
        }
    }

}
