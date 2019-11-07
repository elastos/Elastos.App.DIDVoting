package org.elastos.pojo;

import java.util.List;

public class VoteTopicObj {
    String topicId;
    Integer maxSelections;
    Long startingHeight;
    Long endHeight;
    Double limitBalance;
    List<Integer> optionList;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getMaxSelections() {
        return maxSelections;
    }

    public void setMaxSelections(Integer maxSelections) {
        this.maxSelections = maxSelections;
    }

    public Long getStartingHeight() {
        return startingHeight;
    }

    public void setStartingHeight(Long startingHeight) {
        this.startingHeight = startingHeight;
    }

    public Long getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(Long endHeight) {
        this.endHeight = endHeight;
    }

    public Double getLimitBalance() {
        return limitBalance;
    }

    public void setLimitBalance(Double limitBalance) {
        this.limitBalance = limitBalance;
    }

    public List<Integer> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Integer> optionList) {
        this.optionList = optionList;
    }
}
