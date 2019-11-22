package org.elastos.service;

import org.elastos.constants.VoteTopicType;
import org.elastos.dto.VoteRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DidVoteServiceTest {
    @Autowired
    DidVoteService didVoteService;
    @Test
    public void getPacketData() throws Exception {
        VoteRecord record = new VoteRecord();
        record.setDid("icyQGvLVsepfJ6PAuL43sTcjh9xjZEW5Ge");
        record.setPublicKey("033CAE6DF91E6A77313601FEF25BAB55B0A4362E399377B0B87661AE5A2CE95A81");
        record.setType(VoteTopicType.VOTE_TOPIC_TYPE_VOTE);
        record.setTopicId("0e7e986d4d3d5569214f00139870d04fd79e9c2226b27e8ab02beb83a70d78b0");
        String packet = didVoteService.getPacketData(record);
    }

}