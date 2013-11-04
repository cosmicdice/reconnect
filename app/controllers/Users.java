package controllers;

import play.*;
import play.mvc.*;
import play.libs.Crypto;
import play.data.validation.Validation;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;
import java.io.File;

import models.*;

public class Users extends Controller {
    
    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            renderArgs.put("userConnected", userConnected);
            renderArgs.put("security", Security.connected());
        } else {
            renderArgs.put("user", null);
        }
    }
    
    public static void index() {
        render();
    }

    public static void signup(String email, String password, String name, String username) {
        if(email!= null && validation.email(email).ok && password != null && name != null && username != null){
            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !username.isEmpty()){
                User user = new User(email, Crypto.passwordHash(password), name, username);
                //user.isAdmin = 1;
                user.save();
                redirect("App.index");
            } else {
                render(email, name, username);
            }
        } else {
            render();
        }
    }
    
    public static void show(String username) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null) {
                User user = User.find("byUsername", username).first();
                render(user, userConnected);
            }
            else {
                render(userConnected);
            }
        } else {
            redirect("Home.index");
        }
    }
    
    public static void edit(String gender, Integer day, Integer month, Integer year, String city, String cellphone, String phone, String departement, String workphone, File picture) {
        if (Security.isConnected()) {
			User userConnected = User.find("byUsername", Security.connected()).first();
            if (gender != null && city != null && day != null && month != null && year != null && cellphone != null && phone != null && departement != null && workphone != null && (picture != null || userConnected.picture != null)) {
                userConnected.gender = gender;
                userConnected.dateOfBirth.setDate(day);
                userConnected.dateOfBirth.setMonth(month);
                userConnected.dateOfBirth.setYear(year);
                userConnected.city = city;
                userConnected.cellphone = cellphone;
                userConnected.phone = phone;
				userConnected.departement = departement;
                userConnected.workphone = workphone;
				if (picture != null) {
					File newFile=Play.getFile("/public/upload/" + picture.getName());
					picture.renameTo(newFile);
					picture.delete();
					userConnected.picture = "/public/upload/" + picture.getName();
		        	flash.success("Success "+newFile.getAbsolutePath());
				}
                userConnected.save();
                redirect("Users.show");
            } else {
                render(userConnected);
            }
        } else {
            redirect("Home.index");
        }
    }
    
    public static void contacts(String username) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null) {
                User contact = User.find("byUsername", username).first();
                if (contact != null && userConnected.username.compareTo(contact.username) != 0) {
                    int isAlreadyAdded = 0;
                    int request = 0;
                    for (Long id : userConnected.contacts){
                        if(contact.id.equals(id))
                            isAlreadyAdded = 1;
                    }
                    for (Long id : userConnected.contactsRequest){
                        if(contact.id.equals(id))
                            request = 1;
                    }
                    if (request == 0 && isAlreadyAdded == 0){
                        userConnected.addContact(contact.id);
                        contact.addContactRequest(userConnected.id);
                        contact.save();
                        userConnected.save();
                    }
                    else if (request == 1 && isAlreadyAdded == 0){
                        userConnected.acceptContact(contact.id);
                        userConnected.save();
                    }
                } 
            }
            ArrayList<String> usernames = new ArrayList<String>();
            ArrayList<String> usernamesRequest = new ArrayList<String>();
            User user;
            for (Long id : userConnected.contacts) {
                user = User.find("byId", id).first();
                usernames.add(user.username);
            }    
            for (Long id : userConnected.contactsRequest) {
                    user = User.find("byId", id).first();
                    usernamesRequest.add(user.username);
            }
            int nbReqs = usernamesRequest.size();
            render(userConnected, usernames, usernamesRequest, nbReqs);
        } else {
            redirect("Home.index");
        }
    }
    public static void deleteContact(String username) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null) {
                User contact = User.find("byUsername", username).first();
                if (contact != null){
                    userConnected.deleteContact(contact.id);
                    if(contact.contacts.indexOf(userConnected.id) != -1)
                        contact.deleteContact(userConnected.id);
                    else if(contact.contactsRequest.indexOf(userConnected.id) != -1)
                        contact.deleteContactRequest(userConnected.id);
                    userConnected.save();
                    contact.save();
                } 
            }   
            redirect("Users.contacts");
        } else {
            redirect("Home.index");
        }
    }
    public static void rejectContact(String username) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null) {
                User contact = User.find("byUsername", username).first();
                if (contact != null){
                    userConnected.deleteContactRequest(contact.id);
                    if(contact.contacts.indexOf(userConnected.id) != -1)
                        contact.deleteContact(userConnected.id);
                    userConnected.save();
                    contact.save();
                } 
            }   
            redirect("Users.contacts");
        } else {
            redirect("Home.index");
        }
    }
    public static void acceptContact(String username) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null) {
                User contact = User.find("byUsername", username).first();
                if (contact != null){
                    userConnected.acceptContact(contact.id);
                    userConnected.save();
                } 
            }   
            redirect("Users.contacts");
        } else {
            redirect("Home.index");
        }
    }

        public static void search(String keyword) {
            if (Security.isConnected()) {
                User userConnected = User.find("byUsername", Security.connected()).first();
                if (keyword != null) {
                    User contact = User.find("byUsername", keyword).first();
                    Boolean found = false;
                    ArrayList<String> usernames = new ArrayList<String>();
                    if (contact != null){
                        usernames.add(contact.username);
                        found = true;
                    } 
                    contact = null;
                    contact = User.find("byName", keyword).first();
                    if (contact != null){
						if (!usernames.contains(contact.username)){
                        	usernames.add(contact.username);
                        	found = true;
						}
                    }
                    contact = null;
                    contact = User.find("byEmail", keyword).first();
                    if (contact != null){
						if (!usernames.contains(contact.username)){
                        	usernames.add(contact.username);
                        	found = true;
						}
                    }
                    if (!found){
                        render(userConnected);
                    }
                    else{
                        render(usernames, userConnected);
                    }
                }
                redirect("Users.contacts");
            } else {
                redirect("Home.index");
            }
        }
}