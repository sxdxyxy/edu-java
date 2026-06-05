-- 清理旧菜单结构 V20260526
-- 删除旧的一级菜单和其下的二级菜单

START TRANSACTION;

-- 需要删除的旧一级菜单code列表
SET @old_menu_codes = 'security_manage,archives_manager,mycourse_trainingPlanList,xmgl,trainPlan,kcgl,person_mgr,data_list,courseware_preview,safety_manage,notice_mgr_add,notice_mgr_detail,myexam_detial,mobile_home,9,banner,public_ziyuan,personal_center,my_resource,my_collection,my_report,course_examDetail,mycourse_courseLearn';

-- 查找旧的一级菜单ID
SELECT GROUP_CONCAT(id) INTO @old_pids FROM sys_menu WHERE code IN (
    'security_manage','archives_manager','mycourse_trainingPlanList','xmgl','trainPlan',
    'kcgl','person_mgr','data_list','courseware_preview','safety_manage',
    'notice_mgr_add','notice_mgr_detail','myexam_detial','mobile_home',
    '9','banner','public_ziyuan','personal_center',
    'my_resource','my_collection','my_report','course_examDetail','mycourse_courseLearn'
) AND is_delete = 0;

-- 显示将要删除的菜单
SELECT '将删除以下一级菜单:' AS '';
SELECT id, name, code FROM sys_menu WHERE code IN (
    'security_manage','archives_manager','mycourse_trainingPlanList','xmgl','trainPlan',
    'kcgl','person_mgr','data_list','courseware_preview','safety_manage',
    'notice_mgr_add','notice_mgr_detail','myexam_detial','mobile_home',
    '9','banner','public_ziyuan','personal_center',
    'my_resource','my_collection','my_report','course_examDetail','mycourse_courseLearn'
) AND is_delete = 0;

-- 软删除旧的一级菜单（设置为is_delete=1）
UPDATE sys_menu SET is_delete = 1, delete_time = NOW()
WHERE code IN (
    'security_manage','archives_manager','mycourse_trainingPlanList','xmgl','trainPlan',
    'kcgl','person_mgr','data_list','courseware_preview','safety_manage',
    'notice_mgr_add','notice_mgr_detail','myexam_detial','mobile_home',
    '9','banner','public_ziyuan','personal_center',
    'my_resource','my_collection','my_report','course_examDetail','mycourse_courseLearn'
) AND is_delete = 0;

-- 软删除旧的二级菜单（pid在旧的一级菜单下的）
UPDATE sys_menu SET is_delete = 1, delete_time = NOW()
WHERE pid IN (
    SELECT id FROM (
        SELECT id FROM sys_menu WHERE code IN (
            'security_manage','archives_manager','mycourse_trainingPlanList','xmgl','trainPlan',
            'kcgl','person_mgr','data_list','courseware_preview','safety_manage',
            'notice_mgr_add','notice_mgr_detail','myexam_detial','mobile_home',
            '9','banner','public_ziyuan','personal_center',
            'my_resource','my_collection','my_report','course_examDetail','mycourse_courseLearn'
        ) AND is_delete = 0
    ) AS old_menus
) AND is_delete = 0;

SELECT '清理完成，已删除旧菜单' AS '';

COMMIT;
