-- Habilidades que muestra el onboarding. Se mantienen los tags genéricos de V1
-- y se agregan los nombres exactos que usa el frontend.
INSERT INTO profile_tags (id, name, description) VALUES
    ('00000000-0000-0000-0000-000000000101', 'Gasfitería', 'Instalaciones sanitarias y reparaciones de agua'),
    ('00000000-0000-0000-0000-000000000102', 'Electricidad', 'Instalaciones y reparaciones eléctricas'),
    ('00000000-0000-0000-0000-000000000103', 'Carpintería', 'Trabajos en madera y muebles'),
    ('00000000-0000-0000-0000-000000000104', 'Huerto y Agricultura', 'Cultivos, huertos urbanos y jardinería'),
    ('00000000-0000-0000-0000-000000000105', 'Albañilería', 'Construcción y acabados'),
    ('00000000-0000-0000-0000-000000000106', 'Pintura y Acabados', 'Pintura de interiores y exteriores'),
    ('00000000-0000-0000-0000-000000000107', 'Costura y Tejido', 'Confección, costura y tejido'),
    ('00000000-0000-0000-0000-000000000108', 'Cuidado de Mascotas', 'Paseo y cuidado de animales'),
    ('00000000-0000-0000-0000-000000000109', 'Clases y Talleres', 'Enseñanza y capacitación'),
    ('00000000-0000-0000-0000-00000000010a', 'Reparación Tecnológica', 'Reparación de equipos y soporte técnico'),
    ('00000000-0000-0000-0000-00000000010b', 'Cocina y Panadería', 'Preparación de alimentos y panadería'),
    ('00000000-0000-0000-0000-00000000010c', 'Voluntariado', 'Participación solidaria en la comunidad')
ON CONFLICT (name) DO NOTHING;
