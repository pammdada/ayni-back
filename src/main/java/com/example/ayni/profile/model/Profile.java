package com.example.ayni.profile.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name = "profiles")
public class Profile {
    @Id private UUID id;
    @Enumerated(EnumType.STRING) @Column(name = "account_type", nullable = false) private AccountType accountType;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false) private String username;
    @Column(nullable = false) private String email;
    private String phone; private String bio;
    @Column(name = "avatar_url") private String avatarUrl;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;
    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(name = "user_profile_tags", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<ProfileTag> tags = new HashSet<>();
    protected Profile() {}
    public Profile(UUID id, AccountType accountType, String fullName, String username, String email) { this.id=id; this.accountType=accountType; this.fullName=fullName; this.username=username; this.email=email; }
    @PrePersist void created() { createdAt=Instant.now(); updatedAt=createdAt; }
    @PreUpdate void updated() { updatedAt=Instant.now(); }
    public UUID getId(){return id;} public AccountType getAccountType(){return accountType;} public String getFullName(){return fullName;} public String getUsername(){return username;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getBio(){return bio;} public String getAvatarUrl(){return avatarUrl;} public Set<ProfileTag> getTags(){return tags;}
    public void update(String fullName, String phone, String bio, String avatarUrl) { this.fullName=fullName; this.phone=phone; this.bio=bio; this.avatarUrl=avatarUrl; }
    public void setTags(Set<ProfileTag> tags) { this.tags=tags; }
}
