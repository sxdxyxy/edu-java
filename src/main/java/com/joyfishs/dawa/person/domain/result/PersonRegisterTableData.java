package com.joyfishs.dawa.person.domain.result;

import com.joyfishs.utils.page.TableDataInfo;

/**
 * @author ykfnb
 */
public class PersonRegisterTableData extends TableDataInfo<PersonListResult> {

    private String invitationCode;

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}

