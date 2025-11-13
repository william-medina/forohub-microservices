package com.williammedina.user_service.domain.user.entity;

import com.williammedina.user_service.domain.profile.entity.ProfileEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity(name = "User")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private boolean accountConfirmed = false;

    @Column(length = 255)
    private String token;

    @Column(name = "token_expiration", nullable = false)
    private LocalDateTime tokenExpiration;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        generateConfirmationToken();
    }

    public void generateConfirmationToken() {
        this.token = UUID.randomUUID().toString();
        this.tokenExpiration = LocalDateTime.now().plusMinutes(20);
    }

    public void clearTokenData() {
        this.token = null;
        this.tokenExpiration = LocalDateTime.now();
    }

    public boolean hasElevatedPermissions() {
        return Set.of("MODERATOR", "INSTRUCTOR", "ADMIN").contains(profile.getName());
    }

    @PrePersist
    public void prePersist() {
        if (this.profile == null) {
            this.profile = new ProfileEntity(4L, "USER");
        }
    }

}
