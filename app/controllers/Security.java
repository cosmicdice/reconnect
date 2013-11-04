package controllers;

import play.libs.Crypto;

import models.User;

public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
        return User.connect(username, Crypto.passwordHash(password)) != null;
    }
    public static void onAuthenticated(){
        redirect("App.index");
    }
    public static void onDisconnected(){
        redirect("Home.index");
    }
}