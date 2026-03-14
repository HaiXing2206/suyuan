-- 第三步：台账与治理能力基础版扩展字段

ALTER TABLE data_element_ledgers
    ADD COLUMN IF NOT EXISTS attachment_urls TEXT,
    ADD COLUMN IF NOT EXISTS business_tags VARCHAR(255),
    ADD COLUMN IF NOT EXISTS risk_tags VARCHAR(255),
    ADD COLUMN IF NOT EXISTS quality_tags VARCHAR(255),
    ADD COLUMN IF NOT EXISTS lineage_info TEXT;
