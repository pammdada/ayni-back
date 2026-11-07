package com.example.ayni.profile.repository;
import com.example.ayni.profile.model.Profile; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface ProfileRepository extends JpaRepository<Profile, UUID> { }
