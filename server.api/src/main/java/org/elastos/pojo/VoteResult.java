package org.elastos.pojo;

public class VoteResult {
    Integer OptionID;
    Long Votes;
    String Result;

    public Integer getOptionID() {
        return OptionID;
    }

    public void setOptionID(Integer optionID) {
        OptionID = optionID;
    }

    public Long getVotes() {
        return Votes;
    }

    public void setVotes(Long votes) {
        Votes = votes;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }
}
