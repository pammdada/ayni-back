CREATE TABLE IF NOT EXISTS profile_reviews (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    task VARCHAR(120) NOT NULL,
    stars INTEGER NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_profile_review_reviewer UNIQUE (profile_id, reviewer_id),
    CONSTRAINT chk_profile_review_stars CHECK (stars BETWEEN 1 AND 5),
    CONSTRAINT chk_no_self_review CHECK (profile_id <> reviewer_id)
);

CREATE INDEX IF NOT EXISTS idx_profile_reviews_profile
    ON profile_reviews(profile_id, created_at DESC);
