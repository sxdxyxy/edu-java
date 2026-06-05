package com.joyfishs.system.enums;

/** 删除状态 **/
public enum YesOrNoState {
    YES(1),NO(0);
    private final int state;

    YesOrNoState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
