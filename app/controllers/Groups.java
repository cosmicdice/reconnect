package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

import models.*;

public class Groups extends Controller {
    
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
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (UsersGroup.findAll() != null) {
                List<UsersGroup> allGroups = UsersGroup.findAll();
				List<UsersGroup> listOfMyGroups = new ArrayList<UsersGroup>();
				int nbGrps = 0;
				for (UsersGroup g : allGroups) {
					for (Long idMember : g.members) {
						if (idMember.equals(userConnected.id)) {
							listOfMyGroups.add(g);
							nbGrps++;
						}
					}
				}
				List<UsersGroup> groups = allGroups;
                render(userConnected, groups, nbGrps, listOfMyGroups);
            }
            else
                render(userConnected);
        } else {
            redirect("Home.index");
        }
    }
    
    public static void add(String name, String desc) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (name != null && desc != null) {
                if (!name.isEmpty() && !desc.isEmpty()) {
                    UsersGroup group = new UsersGroup(name, desc, userConnected.id);
                    group.save();
                }
            }   
            redirect("Groups.index");
        } else {
            redirect("Home.index");
        }
    }
    
    public static void show(Long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            UsersGroup group = UsersGroup.find("byId", id).first();
            ArrayList<String> usernames = new ArrayList<String>();
            User user;
            for (Long idMember : group.members) {
                user = User.find("byId", idMember).first();
                usernames.add(user.username);
            }
            render(userConnected, group, usernames);
        } else {
                redirect("Home.index");
        }
    }
    
    public static void join(Long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            UsersGroup group = UsersGroup.find("byId", id).first();
            int isPresent = 0;
            for(Long idMember : group.members){
                if (userConnected.id.equals(idMember))
                    isPresent = 1;
            }
            if (isPresent == 0){
                group.addMember(userConnected.id);
                group.save();
            }
            redirect("/groups/show/" + id);
        } else {
                redirect("Home.index");
        }
    }
    public static void removeMember(String username, Long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (username != null && id != null) {
                User user = User.find("byUsername", username).first();
                UsersGroup group = UsersGroup.find("byId", id).first();
                if (userConnected.id.equals(group.owner) || userConnected.isAdmin == 1) {
                    if (user.id.equals(group.owner))
                        group.owner = new Long(0);
                    group.deleteMember(user.id);
                    group.save();
                }
            }   
            redirect("/groups/show/" + id);
        } else {
            redirect("Home.index");
        }
    }
	public static void newMessage(Long id) {
        if (Security.isConnected()) {
			User userConnected = User.find("byUsername", Security.connected()).first();
			render(userConnected, id);
        } else {
            redirect("Home.index");
        }
    }
}