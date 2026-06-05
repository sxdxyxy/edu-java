package com.joyfishs.dawa.person.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.domain.param.PersonListQueryRequest;
import com.joyfishs.dawa.person.entity.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {

    List<Person> queryList(PersonListQueryRequest person);

    List<Person> queryListByOrg(PersonListQueryRequest person);

    /**
     * userId查询信息
     */
    @Select(" select * from xm_person where user_id = #{userId} and is_delete = 0 ")
    Person getByUserId(@Param("userId") Long userId);

    /**
     * 检查此身份证号码除自己外是否有其它人已注册
     *
     * @param personId
     * @param idCardNo
     * @return 重复人员的id
     */
    @Select("SELECT id FROM  xm_person WHERE id_card_no = #{idCardNo} AND id!=#{personId}")
    Long repeatIdCardNoCheck(@Param("personId") Long personId, @Param("idCardNo") String idCardNo);

    @Select("select * from xm_person where  phone = #{phone} order by create_time desc limit 1")
    public Person findByPhone(@Param("phone") String phone);

    /**
     * 查询组织下的人列表
     *
     * @param orgId
     * @return
     */
    @Select(" select a.*  " +
            " from xm_person a  " +
            "         left join sys_org c on a.org_id = c.id  " +
            " where a.is_delete = 0  " +
            "  and c.is_delete = 0  " +
            "  and a.state = 1  " +
            "  and (a.org_id = #{orgId} or c.pids like concat('%[', #{orgId}, ']%')) ")
    List<Person> findListByOrgId(Long orgId);
}
