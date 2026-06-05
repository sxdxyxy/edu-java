package com.joyfishs.dawa.course.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 题目类型:
 * 1-单选题 2-多选题 3-判断题  4-简答题 5-填空题
 */
public enum QuestionType implements IEnum<Integer> {
    SingleChoice(1, "单选题"),
    MultipleChoice(2, "多选题"),
    TrueOrFalse(3, "判断题"),
    ShortAnswer(4, "简答题"),
    FillInTheBlank(5, "填空题");

    @EnumValue
    @JsonValue
    private final int value;
    private final String desc;

    QuestionType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
    public String getDesc() {
        return desc;
    }
    @JsonCreator
    public static QuestionType ofValue(int code) {
        for (QuestionType value : values()) {
            if (code == value.getValue().intValue()) {
                return value;
            }
        }
        return null;
    }
}
