package com.lifebook.Model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AppUserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String fullName;

    @Column(name = "email", nullable = false)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;

    private String profilePic;

    @OneToOne(mappedBy = "detail")
    private AppUser currentUser;

    @OneToMany(mappedBy = "detail")
    private Set<AppUser> followers;

    @OneToMany(mappedBy = "creator")
    private Set<UserPost> posts;

    @OneToMany(mappedBy = "settingUser")
    private Set<Setting> settings;

    public AppUserDetails() {
        this.followers = new HashSet<>();
        this.posts = new HashSet<>();
        this.settings = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public AppUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AppUser currentUser) {
        this.currentUser = currentUser;
    }

    public Set<AppUser> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<AppUser> followers) {
        this.followers = followers;
    }

    public Set<UserPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<UserPost> posts) {
        this.posts = posts;
    }

    public Set<Setting> getSettings() {
        return settings;
    }

    public void setSettings(Set<Setting> settings) {
        this.settings = settings;
    }
}
