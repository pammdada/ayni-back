package com.example.ayni.profile.repository;

import com.example.ayni.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    boolean existsByUsername(String username);

    @Query("select distinct p from Profile p left join fetch p.tags where p.id = :id")
    Optional<Profile> findWithTagsById(@Param("id") UUID id);

    @Query("select distinct p from Profile p left join fetch p.tags where p.id in :ids")
    List<Profile> findAllWithTagsByIdIn(@Param("ids") Collection<UUID> ids);

    /**
     * Contadores del perfil en una sola consulta. Antes se hacían 5 consultas
     * (existencia + 4 conteos) y con la base remota cada una paga la latencia
     * de red, por eso el perfil tardaba en cargar.
     */
    @Query(value = """
            select
                (select count(*) from posts p where p.author_id = pr.id and p.status <> 'DELETED'),
                (select count(*) from profile_follows f where f.followed_id = pr.id),
                (select count(*) from profile_follows f where f.follower_id = pr.id),
                (select count(*) from profile_follows f where f.follower_id = :viewer and f.followed_id = pr.id)
            from profiles pr
            where pr.id = :id
            """, nativeQuery = true)
    List<Object[]> stats(@Param("id") UUID id, @Param("viewer") UUID viewer);

    /**
     * Inserción idempotente: si dos peticiones intentan crear el mismo perfil a la vez,
     * la segunda no falla ni duplica la fila.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            insert into profiles (id, account_type, full_name, username, email, sector, created_at, updated_at)
            values (:id, :accountType, :fullName, :username, :email, :sector, now(), now())
            on conflict (id) do nothing
            """, nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id,
                       @Param("accountType") String accountType,
                       @Param("fullName") String fullName,
                       @Param("username") String username,
                       @Param("email") String email,
                       @Param("sector") String sector);
}
