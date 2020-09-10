package com.epam.rd.java.basic.practice8.db.entity;

import java.util.Objects;

public class User {

    private long id;
    private String login;

    public User() {
    }

    private User(String login) {
        this.login = login;
    }

    public static User createUser(String login) {
        return new User(login);
    }


    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(login, user.login);
    }
}
