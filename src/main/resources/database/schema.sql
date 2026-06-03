CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS sticker_packs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    is_saved BOOLEAN DEFAULT FALSE,
    visual_style VARCHAR(100),
    irony_level INT,
    analysis_mode VARCHAR(50),
    version INT DEFAULT 1,
    parent_pack_id BIGINT,
    user_id BIGINT NOT NULL,
    
    CONSTRAINT fk_packer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_parent_pack FOREIGN KEY (parent_pack_id) REFERENCES sticker_packs(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS stickers (
    id BIGSERIAL PRIMARY KEY,
    emotion VARCHAR(255) NOT NULL,
    image_bytes OID,
    pack_id BIGINT NOT NULL,
    
    CONSTRAINT fk_sticker_pack FOREIGN KEY (pack_id) REFERENCES sticker_packs(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS gigachat_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    prompt TEXT,
    status VARCHAR(50),
    status_code INT,
    response_or_error TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_packs_user ON sticker_packs(user_id);
CREATE INDEX IF NOT EXISTS idx_packs_parent ON sticker_packs(parent_pack_id);
