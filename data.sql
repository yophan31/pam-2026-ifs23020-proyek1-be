-- Tabel Users
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    photo VARCHAR(255) NULL,
    about TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Tabel Refresh Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    refresh_token TEXT NOT NULL,
    auth_token TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

-- Tabel Sport Events (Daftar Event Olahraga)
CREATE TABLE IF NOT EXISTS sport_events (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    cover TEXT NULL,

    -- Kolom khusus event olahraga
    kategori VARCHAR(100) NOT NULL,           -- sepak bola, basket, voli, dll
    tanggal_event VARCHAR(100) NOT NULL,      -- tanggal pelaksanaan event
    lokasi VARCHAR(255) NOT NULL,             -- tempat/stadion
    status VARCHAR(50) DEFAULT 'akan datang' NOT NULL, -- akan datang / berlangsung / selesai
    penyelenggara VARCHAR(150) NOT NULL,      -- nama penyelenggara / organisasi

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );
