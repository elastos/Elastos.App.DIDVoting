package org.elastos.pojo;

import java.io.Serializable;

public class VoteOption  implements Serializable {
    Integer OptionID;
    String Name;
    String Desc;

    public Integer getOptionID() {
        return OptionID;
    }

    public void setOptionID(Integer optionID) {
        OptionID = optionID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }
}
