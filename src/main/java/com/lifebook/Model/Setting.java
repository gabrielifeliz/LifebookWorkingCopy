package com.lifebook.Model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    private String newsType;

    @ManyToMany
    private Set<AppUser> settingUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public Set<AppUser> getSettingUser() {
        return settingUser;
    }

    public void setSettingUser(Set<AppUser> settingUser) {
        this.settingUser = settingUser;
    }

    public Setting() {
        this.settingUser = new HashSet<>();
    }


}
