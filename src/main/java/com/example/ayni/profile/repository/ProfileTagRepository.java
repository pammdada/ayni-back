package com.example.ayni.profile.repository;
import com.example.ayni.profile.model.ProfileTag; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ProfileTagRepository extends JpaRepository<ProfileTag, UUID> { Set<ProfileTag> findByNameIn(Collection<String> names); }
