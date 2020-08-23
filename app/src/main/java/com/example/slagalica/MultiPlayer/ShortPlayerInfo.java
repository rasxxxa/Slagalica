package com.example.slagalica.MultiPlayer;

import androidx.annotation.Nullable;

public class ShortPlayerInfo {
    String username;
    String name;
    String lastName;

    public ShortPlayerInfo() {
    }

    public ShortPlayerInfo(String username, String name, String lastName) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ShortPlayerInfo info = (ShortPlayerInfo)obj;
        return info.getUsername().equals(this.username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
