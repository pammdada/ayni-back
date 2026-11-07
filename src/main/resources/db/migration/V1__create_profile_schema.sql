CREATE TABLE profiles (
    id UUID PRIMARY KEY,
    account_type VARCHAR(20) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    phone VARCHAR(30),
    bio VARCHAR(500),
    avatar_url VARCHAR(2048),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_profiles_account_type CHECK (account_type IN ('INDIVIDUAL', 'ORGANIZATION'))
);

CREATE TABLE profile_tags (
    id UUID PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE user_profile_tags (
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES profile_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, tag_id)
);

INSERT INTO profile_tags (id, name, description) VALUES
('00000000-0000-0000-0000-000000000001', 'VECINO', 'Persona que participa en su vecindario'),
('00000000-0000-0000-0000-000000000002', 'EMPRENDEDOR', 'Persona con un emprendimiento'),
('00000000-0000-0000-0000-000000000003', 'VOLUNTARIO', 'Persona disponible para ayudar'),
('00000000-0000-0000-0000-000000000004', 'PROFESIONAL', 'Persona que ofrece conocimiento profesional'),
('00000000-0000-0000-0000-000000000005', 'ESTUDIANTE', 'Persona en formación'),
('00000000-0000-0000-0000-000000000006', 'OTRO', 'Otra etiqueta de perfil');
