package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application.old extends Controller {
    @Before
    static void setConnectedUser() {
         if(Security.isConnected()) {
             User user = User.find("byUsername", Security.connected()).first();
             renderArgs.put("user", user);
             renderArgs.put("security", Security.connected());
         }
         else {
             renderArgs.put("user", new User("", "", "", "No"));
         }
     }
    
    public static void index() {
        if(!Security.isConnected())
            render();
        else
            redirect("Application.app");
    }
    public static void app() {
        if(Security.isConnected())
            render();
        else
            redirect("Application.index");
    }
    public static void profile() {
        if(Security.isConnected()){
            User user = User.find("byUsername", Security.connected()).first();
            render(user);
        }
        else
            redirect("Application.index");
    }
    public static void editProfile(String gender, String date, String city, String cellphone, String phone, String workphone) {
        if(Security.isConnected()){
            if(gender != null && city != null && date != null && cellphone != null && phone != null && workphone != null){
                    User user = User.find("byUsername", Security.connected()).first();
                    user.gender = gender;
                    //user.dateOfBirth = date;
                    user.city = city;
                    user.cellphone = cellphone;
                    user.phone = phone;
                    user.workphone = workphone;
                    user.save();
                    redirect("Application.profile");
            } else {
                User user = User.find("byUsername", Security.connected()).first();
                render(user);
            }
        } else
            redirect("Application.index");
    }
}