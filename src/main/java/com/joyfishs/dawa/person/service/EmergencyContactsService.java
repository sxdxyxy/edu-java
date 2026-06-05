package com.joyfishs.dawa.person.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.EmergencyContacts;
import com.joyfishs.dawa.person.mapper.EmergencyContactsMapper;

/**
 * @author ykfnb
 */
@Service
public class EmergencyContactsService extends ServiceImpl<EmergencyContactsMapper, EmergencyContacts> {
    public List<EmergencyContacts> listByPersonId(Long personId) {
        return baseMapper.listByPersonId(personId);
    }

    public Boolean addContact(EmergencyContacts param) {
        return this.save(param);
    }

    public Boolean removeContact(Long contactId) {
        return this.removeById(contactId);
    }
}
