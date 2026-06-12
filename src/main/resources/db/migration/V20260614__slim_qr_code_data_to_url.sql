-- ============================================================
-- V20260614__slim_qr_code_data_to_url.sql
-- 批次 D6: qr_code_data 字段从 TEXT 改为 VARCHAR(512) 存 URL
--
-- 背景: 之前 SafetyCodeService 把二维码以 data:image/png;base64,...
--       字符串存到 safety_codes.qr_code_data (TEXT). 每行多 5-10KB.
--       扫表/备份/慢查询都受拖累.
-- 改造: 二维码字节流交给 QrCodeStorage (dev-sts 走 Local, prod 走 OSS),
--       只把 URL 存到 qr_code_data 字段.
-- 数据迁移: dev-sts 旧 base64 数据直接 truncate (测试数据).
--           生产环境需先评估是否需要 URL 备份.
-- ============================================================

ALTER TABLE safety_codes MODIFY COLUMN qr_code_data VARCHAR(512) NULL COMMENT '二维码 URL (D6 改造: 从 base64 改为 QrCodeStorage 返回的 URL)';

-- 顺手清理旧 base64 残留数据 (dev-sts 可直接清; 生产环境评估后决定)
-- UPDATE safety_codes SET qr_code_data = NULL WHERE LENGTH(qr_code_data) > 512;
-- 上面这条改成 WHERE LENGTH > 512 因为旧 base64 远超 512 字符,UPDATE 时 MySQL 会先按旧 TEXT 允许但写入新 VARCHAR 会被截断
-- dev-sts 阶段: 直接清, 反正 D6 改造后下次生成二维码会自动写 URL
UPDATE safety_codes SET qr_code_data = NULL;
