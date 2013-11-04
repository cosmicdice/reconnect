package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Admin extends Controller {
    
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
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (userConnected.isAdmin == 1) {
                List<User> users = User.findAll();
                List<UsersGroup> groups = UsersGroup.findAll();
                render(userConnected, users, groups);
            } else {
                redirect("App.index");
            }
        } else {
            redirect("Home.index");
        }
    }
    
    public static void edit(long id, String name, String username, String email, String gender, Integer day, Integer month, Integer year, String city, String cellphone, String phone, String departement, String workphone, String isAdmin) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (userConnected.isAdmin == 1) {
                User user = User.find("byId", id).first();
                if (name != null && username != null && email != null && gender != null && city != null && day != null && month != null && year != null && cellphone != null && phone != null && departement != null && workphone != null && isAdmin != null) {
                    user.name = name;
                    user.username = username;
                    user.email = email;
                    user.gender = gender;
                    user.dateOfBirth.setDate(day);
                    user.dateOfBirth.setMonth(month);
                    user.dateOfBirth.setYear(year);
                    user.city = city;
                    user.cellphone = cellphone;
                    user.phone = phone;
					user.departement = departement;
                    user.workphone = workphone;
                    user.isAdmin = (isAdmin.equals("Yes")) ? 1 : 0;
                    user.save();
                    redirect("Admin.index");
                } else {
                    render(user);
                }
            } else {
                redirect("App.index");
            }
        } else {
            redirect("Home.index");
        }
    }
    
    public static void delete(long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (userConnected.isAdmin == 1) {
                User user = User.find("byId", id).first();
                user.delete();
                redirect("Admin.index");
            } else {
                redirect("App.index");
            }
        } else {
            redirect("Home.index");
        }
    }
    public static void editGroup(Long id, String name, String desc) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (userConnected.isAdmin == 1) {
                UsersGroup group = UsersGroup.find("byId", id).first();
                if (name != null && desc != null) {
                    group.name = name;
                    group.desc = desc;
                    group.save();
                    redirect("Admin.index");
                } else {
                    render(group);
                }
            } else {
                redirect("App.index");
            }
        } else {
            redirect("Home.index");
        }
    }
    
    public static void deleteGroup(long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (userConnected.isAdmin == 1) {
                UsersGroup group = UsersGroup.find("byId", id).first();
                group.delete();
                redirect("Admin.index");
            } else {
                redirect("App.index");
            }
        } else {
            redirect("Home.index");
        }
    }
}