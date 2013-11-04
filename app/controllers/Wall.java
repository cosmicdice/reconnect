package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import javax.persistence.*;


import models.*;

public class Wall extends Controller {
    
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
			List<Status> statuses = Status.find("select s from Status as s where s.owner=? order by s.id desc", userConnected.id).fetch();
            render(userConnected, statuses);
        } else {
            redirect("Home.index");
        }
    }
	public static void newStatus(String content) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (content != null) {
				Date date = new Date();
				Long type = new Long(1);
                Status status = new Status(userConnected.id, date, type, content);
                status.save();
                redirect("Wall.index");
            }
            else
                render(userConnected);
        } else {
            redirect("Home.index");
        }
    }
	public static void delete(long id) {
        if (Security.isConnected()) {
                Status status = Status.find("byId", id).first();
                status.delete();
                redirect("Wall.index");
        } else {
            redirect("Home.index");
        }
    }
}