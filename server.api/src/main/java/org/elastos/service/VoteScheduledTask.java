package org.elastos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class VoteScheduledTask {

    private Logger logger = LoggerFactory.getLogger(VoteScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean onFlag = false;

    public void setOnFlag(boolean onFlag) {
        this.onFlag = onFlag;
    }

    @Autowired
    VoteRecordComponent voteRecordComponent;

    @Autowired
    VoteComponent voteComponent;

//    @Scheduled(initialDelay = 60*1000, fixedDelay = 60*1000)
    @Scheduled(fixedDelay = 60*1000)
    public void recordTask() {
        if (!onFlag) {
            return;
        }
        logger.debug("exchangeTask begin at:"+ dateFormat.format(new Date()));
        voteRecordComponent.recordTask();
        voteComponent.checkVoteTask();
        logger.debug("exchangeTask finish at:"+ dateFormat.format(new Date()));
    }
}
