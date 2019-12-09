package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elastos.constants.VoteTopicType;
import org.elastos.dto.VoteRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DidVoteServiceTest {
    @Autowired
    DidVoteService didVoteService;

    @Test
    public void createPacketData() throws Exception {
        VoteRecord record = new VoteRecord();
        record.setDid("icyQGvLVsepfJ6PAuL43sTcjh9xjZEW5Ge");
        record.setPublicKey("033CAE6DF91E6A77313601FEF25BAB55B0A4362E399377B0B87661AE5A2CE95A81");
        record.setType(VoteTopicType.VOTE_TOPIC_TYPE_OBJECT);
        record.setTopicId("0e7e986d4d3d5569214f00139870d04fd79e9c2226b27e8ab02beb83a70d78b0");
        String data = "{\"Signature\":\"d5840f74024312fd1761fe24ccbdb1db630bc24be1dfc4ef1788c6209236300dbe02f32c3a2bd14964e1a500a433e57cd0e55307689c6442abf3fb6a30759ac4\",\"DataStr\":\"%7B%22Timestamp%22%3A1569398290128%2C%22DID%22%3A%22iTUs69fE2JKuT3y6vp3PNdvM5Z28KDAwKV%22%2C%22PublicKey%22%3A%2203beca0cb558bc1c028a19a82f37966b48f39d579bd44ef92029c2a3b1fd5794e5%22%2C%22RequestedConent%22%3A%22%7B%5C%22Name%5C%22%3A%5C%22testData%5C%22%2C%5C%22Desc%5C%22%3A%5C%22%5C%22%2C%5C%22Type%5C%22%3A%5C%22singleChoice%5C%22%2C%5C%22Max-Selections%5C%22%3A2%2C%5C%22Starting-height%5C%22%3A231732%2C%5C%22End-height%5C%22%3A232732%2C%5C%22Options%5C%22%3A%5B%7B%5C%22OptionID%5C%22%3A1%2C%5C%22Name%5C%22%3A%5C%22%E9%87%8D%E8%A6%81%5C%22%2C%5C%22Desc%5C%22%3A%5C%22%5C%22%7D%2C%7B%5C%22OptionID%5C%22%3A2%2C%5C%22Name%5C%22%3A%5C%22%E4%B8%8D%E9%87%8D%E8%A6%81%5C%22%2C%5C%22Desc%5C%22%3A%5C%22%5C%22%7D%5D%7D%22%2C%22RequesterDID%22%3A%22ipFFpL1JwPRQKfhmxak8r1wWEbdkJ3rXZu%22%2C%22UseStatement%22%3A%22didvote%22%7D\"}";
        JSONObject obj = JSON.parseObject(data);

        Map<String, Object> packet = new HashMap<>();
        packet.put("packet_num", 500);
        packet.put("packet_amt", 1.0);
        packet.put("packet_type",0);
        packet.put("packet_creator","Elaphant wallet");
        obj.put("CreatePacket", packet);
        record.setPropertyValue(obj.toJSONString());

        String packetRet = didVoteService.procPacketData(record);
    }

    @Test
    public void getPacketData() throws Exception {
        VoteRecord record = new VoteRecord();
        record.setDid("icyQGvLVsepfJ6PAuL43sTcjh9xjZEW5Ge");
        record.setPublicKey("033CAE6DF91E6A77313601FEF25BAB55B0A4362E399377B0B87661AE5A2CE95A81");
        record.setType(VoteTopicType.VOTE_TOPIC_TYPE_VOTE);
        record.setTopicId("0e7e986d4d3d5569214f00139870d04fd79e9c2226b27e8ab02beb83a70d78b0");
        String packet = didVoteService.procPacketData(record);
    }

    @Test
    public void delPacketData() throws Exception {
        VoteRecord record = new VoteRecord();
        record.setDid("icyQGvLVsepfJ6PAuL43sTcjh9xjZEW5Ge");
        record.setPublicKey("033CAE6DF91E6A77313601FEF25BAB55B0A4362E399377B0B87661AE5A2CE95A81");
        record.setType(VoteTopicType.VOTE_TOPIC_TYPE_RESULT);
        record.setTopicId("0e7e986d4d3d5569214f00139870d04fd79e9c2226b27e8ab02beb83a70d78b0");
        String packet = didVoteService.procPacketData(record);
    }

}