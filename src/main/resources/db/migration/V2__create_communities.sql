CREATE TABLE communities (
    id UUID PRIMARY KEY, name VARCHAR(120) NOT NULL, description VARCHAR(1000), location_name VARCHAR(255),
    latitude DOUBLE PRECISION, longitude DOUBLE PRECISION, created_by UUID NOT NULL REFERENCES profiles(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE community_members (
    id UUID PRIMARY KEY, community_id UUID NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE, role VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, UNIQUE(community_id, user_id),
    CONSTRAINT chk_member_role CHECK (role IN ('ADMIN', 'MEMBER')), CONSTRAINT chk_member_status CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED', 'BANNED'))
);
