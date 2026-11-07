package com.example.ayni.profile.model;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="profile_tags") public class ProfileTag { @Id private UUID id; @Column(nullable=false) private String name; protected ProfileTag(){} public UUID getId(){return id;} public String getName(){return name;} }
