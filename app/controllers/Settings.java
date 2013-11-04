package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import play.libs.Crypto;
import play.data.validation.Validation;


import models.*;

public class Settings extends Controller {
    
    @Before
    static void setConnectedUser() {
         if (Security.isConnected()) {
             User userConnected = User.find("byUsername", Security.connected()).first();
             renderArgs.put("userConnected", userConnected);
             renderArgs.put("security", Security.connected());
         } else {
             renderArgs.put("userConnected", null);
         }
     }
    
    public static void index() {
        if (Security.isConnected()) {
            render();
        } else {
            redirect("Home.index");
        }
    }
    
    public static void account(String email, String currentPassword, String newPassword, String reNewPassword, String name, String username) {
        if (Security.isConnected()) {
            if (email != null && validation.email(email).ok && currentPassword != null && name != null && username != null && ((newPassword == null && reNewPassword == null) || (newPassword.compareTo(reNewPassword) == 0))) {
                User userConnected = User.find("byUsername", Security.connected()).first();
                if(userConnected.password.compareTo(Crypto.passwordHash(currentPassword)) == 0 && !email.isEmpty() && !currentPassword.isEmpty() && !name.isEmpty() && !username.isEmpty()) {
                    userConnected.email = email;
                    userConnected.name = name;
                    userConnected.username = username;
                    userConnected.password = (newPassword != null && !newPassword.isEmpty()) ? Crypto.passwordHash(newPassword) : Crypto.passwordHash(currentPassword);
                    userConnected.save();
                    redirect("Settings.index");
                }
                else {
                    redirect("Settings.account");
                }
            } else {
                User userConnected = User.find("byUsername", Security.connected()).first();
                render(userConnected);
            }
        } else {
            redirect("Home.index");
        }
    }
}