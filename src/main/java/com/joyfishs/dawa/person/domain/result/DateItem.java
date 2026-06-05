package com.joyfishs.dawa.person.domain.result;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DateItem {
    private LocalDate date;
    private String name;
    private Long id;
    //状态描述
    private String state;
    // 0-课程 1-签名文件 2-证件
    private int type;
    private int style;
    private String fileUrl;

    public DateItem(LocalDate date, String name, Long id, String state, int type) {
        this.date = date;
        this.name = name;
        this.id = id;
        this.state = state;
        this.type = type;
    }

    public DateItem setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public DateItem setStyle() {
        if (this.type == 0) {
            if (Integer.valueOf(state) >= 60) {
                this.state = "合格(" + state + "分)";
                this.style = 1;
            } else {
                this.state = "不合格(" + state + "分)";
                this.style = 2;
            }
        }
        if (this.type == 1) {
            this.style = 1;
        }
        if (this.type == 2) {
            this.style = 0;
        }
        return this;
    }

}
