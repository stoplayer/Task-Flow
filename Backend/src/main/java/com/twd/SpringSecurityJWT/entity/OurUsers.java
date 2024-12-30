package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "ourusers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OurUsers implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateInscription;

    // Profile picture fields with LONGBLOB handling
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    @Column(name = "profile_picture_type")
    private String profilePictureType;

    @Column(name = "profile_picture_name")
    private String profilePictureName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    protected void onCreate() {
        this.dateInscription = new Date();
    }

    // Profile picture methods
    public void setProfilePicture(byte[] profilePicture, String contentType, String fileName) {
        this.profilePicture = profilePicture;
        this.profilePictureType = contentType;
        this.profilePictureName = fileName;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public String getProfilePictureType() {
        return profilePictureType;
    }

    public String getProfilePictureName() {
        return profilePictureName;
    }
}
