package com.joyfishs.dawa.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.entity.RecommendationRule;
import com.joyfishs.dawa.course.mapper.RecommendationRuleMapper;
import com.joyfishs.dawa.course.service.CourseRecommendationService;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.mapper.SysMenuMapper;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course/recommendation")
@Api(tags = "智能课程推送策略")
public class CourseRecommendationController {

    @Autowired
    private CourseRecommendationService recommendationService;

    @Autowired
    private RecommendationRuleMapper ruleMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    /**
     * 将“推荐规则设置”自动添加到系统菜单中 (子菜单)
     */
    @PostMapping("/rules/registerMenu")
    public AjaxResult<String> registerMenu() {
        // 1. 查找父级菜单“系统设置”或“系统管理”
        LambdaQueryWrapper<SysMenu> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.in(SysMenu::getName, "系统设置", "系统管理");
        SysMenu parent = menuMapper.selectOne(parentWrapper);
        
        if (parent == null) {
            return AjaxResult.error("未找到父级菜单‘系统设置’或‘系统管理’，请手动检查系统菜单名称");
        }

        // 2. 检查是否已经存在 (根据名称查找，无论父级是谁)
        LambdaQueryWrapper<SysMenu> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysMenu::getName, "推荐规则设置");
        SysMenu menu = menuMapper.selectOne(childWrapper);
        
        boolean isNew = false;
        if (menu == null) {
            menu = new SysMenu();
            isNew = true;
        }

        // 3. 构建菜单属性 (迁移到新父级)
        menu.setName("推荐规则设置");
        menu.setCode("recommendation_rule_setting");
        menu.setIcon("setting");
        menu.setPid(parent.getId());
        menu.setPids(parent.getPids() + "[" + parent.getId() + "],");
        menu.setRouter("/mycourse/recommendation-rules");
        menu.setComponent("mycourse/mycourseList/RecommendationRuleSetting");
        menu.setVisible(1);
        menu.setType(1); // 菜单类型 (1=菜单)
        
        if (isNew) {
            Integer maxCode = menuMapper.findMaxIntCodeByParentId(parent.getId());
            menu.setIntCode(maxCode != null ? maxCode + 1 : 1);
            menuMapper.insert(menu);
        } else {
            menuMapper.updateById(menu);
        }
        return AjaxResult.success("菜单已成功移动至“" + parent.getName() + "”目录下，请刷新页面查看");
    }

    /**
     * 获取当前所有推荐规则及其状态
     */
    @GetMapping("/rules")
    public AjaxResult<List<RecommendationRule>> getRules() {
        return AjaxResult.success(ruleMapper.selectList(new LambdaQueryWrapper<RecommendationRule>().orderByAsc(RecommendationRule::getId)));
    }

    /**
     * 更新推荐规则启用状态
     */
    @PostMapping("/rules/update")
    public AjaxResult<String> updateRules(@RequestBody List<RecommendationRule> rules) {
        for (RecommendationRule rule : rules) {
            ruleMapper.updateById(rule);
        }
        return AjaxResult.success("规则更新成功");
    }

    /**
     * 初始化默认规则 (一次性使用或管理端手动触发)
     */
    @PostMapping("/rules/init")
    public AjaxResult<String> initRules() {
        // 1. 初始化规则
        if (ruleMapper.selectCount(null) == 0) {
            String[][] defaultRules = {
                {"job", "岗位匹配", "根据人员岗位/工种推荐相关课程"},
                {"profession", "专业匹配", "根据人员所属组织/专业条线推荐课程"},
                {"gender", "性别关联", "根据性别特征推荐权益或保护类课程"},
                {"age", "年龄关联", "根据年龄特征推荐健康或关怀类课程"},
                {"degree", "学历匹配", "根据学历层次(本科/高职等)调整推荐权重"},
                {"violation", "违章记录关联", "根据人员历史违章记录推送相关警示教育课程"},
                {"hidden_danger", "隐患关联", "根据人员上报或负责区域的隐患类型推荐课程"}
            };

            for (String[] r : defaultRules) {
                RecommendationRule rule = new RecommendationRule();
                rule.setRuleCode(r[0]);
                rule.setRuleName(r[1]);
                rule.setDescription(r[2]);
                rule.setEnabled(1); // 默认开启
                ruleMapper.insert(rule);
            }
        }
        
        // 2. 自动注册菜单
        registerMenu();

        return AjaxResult.success("由于新功能发布，规则与菜单已初始化完成，请刷新页面查看");
    }

    @ApiOperation("获取当前登录学员的个性化推荐课程")
    @GetMapping("/my")
    public AjaxResult<List<Course>> getMyRecommendations() {
        Long userId = SecurityUtil.getUserId();
        List<Course> recommendations = recommendationService.recommendCoursesForUser(userId);
        return AjaxResult.success(recommendations);
    }

    /**
     * 诊断：检查菜单配置
     */
    @GetMapping("/rules/checkMenu")
    public AjaxResult<?> checkMenu() {
        LambdaQueryWrapper<SysMenu> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(SysMenu::getName, "给我推荐");
        SysMenu parent = menuMapper.selectOne(parentWrapper);
        
        LambdaQueryWrapper<SysMenu> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysMenu::getName, "推荐规则设置");
        List<SysMenu> children = menuMapper.selectList(childWrapper);
        
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("parent", parent);
        res.put("children", children);
        return AjaxResult.success(res);
    }
}
