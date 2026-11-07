CREATE TABLE categories (id UUID PRIMARY KEY, name VARCHAR(80) NOT NULL UNIQUE, description VARCHAR(255), type VARCHAR(20) NOT NULL);
CREATE TABLE listings (
 id UUID PRIMARY KEY, owner_id UUID NOT NULL REFERENCES profiles(id), community_id UUID REFERENCES communities(id), category_id UUID REFERENCES categories(id),
 type VARCHAR(20) NOT NULL, title VARCHAR(160) NOT NULL, description VARCHAR(2000) NOT NULL, price DECIMAL(12,2), currency VARCHAR(3), image_url VARCHAR(2048), status VARCHAR(20) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT chk_listing_type CHECK(type IN ('PRODUCT','SERVICE')), CONSTRAINT chk_listing_status CHECK(status IN ('ACTIVE','PAUSED','SOLD_OUT','DELETED'))
);
CREATE INDEX idx_listings_community_category ON listings(community_id, category_id);
INSERT INTO categories(id,name,description,type) VALUES
('10000000-0000-0000-0000-000000000001','Alimentos','Comida y bebidas','PRODUCT'),('10000000-0000-0000-0000-000000000002','Hogar','Artículos para el hogar','PRODUCT'),('10000000-0000-0000-0000-000000000003','Servicios profesionales','Servicios especializados','SERVICE');
