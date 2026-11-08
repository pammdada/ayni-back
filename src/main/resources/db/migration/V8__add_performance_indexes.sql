-- Índices de rendimiento para las consultas del feed, perfiles y comunidades.

-- community_members se consulta por user_id en cada carga del feed y en /communities/mine.
CREATE INDEX IF NOT EXISTS idx_community_members_user_status
    ON community_members(user_id, status);
CREATE INDEX IF NOT EXISTS idx_community_members_community_status
    ON community_members(community_id, status);

-- listings: feed por estado/fecha y perfil por propietario.
CREATE INDEX IF NOT EXISTS idx_listings_status_created
    ON listings(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_listings_owner_status
    ON listings(owner_id, status);

-- help_posts: feed por estado/fecha y perfil por usuario.
CREATE INDEX IF NOT EXISTS idx_help_posts_status_created
    ON help_posts(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_help_posts_user
    ON help_posts(user_id);

-- post_likes: "me gusta" del usuario actual y conteos.
CREATE INDEX IF NOT EXISTS idx_post_likes_user
    ON post_likes(user_id);

-- post_comments: conteos por publicación + estado y comentarios por autor.
CREATE INDEX IF NOT EXISTS idx_post_comments_post_status
    ON post_comments(post_id, status);
CREATE INDEX IF NOT EXISTS idx_post_comments_author
    ON post_comments(author_id);

-- sync_operations: búsqueda de operaciones por usuario.
CREATE INDEX IF NOT EXISTS idx_sync_operations_user
    ON sync_operations(user_id);

-- profile_reviews: reseñas hechas por un usuario.
CREATE INDEX IF NOT EXISTS idx_profile_reviews_reviewer
    ON profile_reviews(reviewer_id);
