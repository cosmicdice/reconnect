package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import javax.persistence.*;


import models.*;

public class Tasks extends Controller {

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
            List<Task> tasks = Task.find("select t from Task as t where t.owner=? and t.done=false order by t.level desc", userConnected.id).fetch();
            List<Task> tasksDone = Task.find("select t from Task as t where t.owner=? and t.done=true order by t.level desc", userConnected.id).fetch();
            render(userConnected, tasks, tasksDone);
        } else {
            redirect("Home.index");
        }
    }

    /*public static void doneList() {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            List<Task> tasks = Task.find("select t from Task as t where t.owner=? and t.done=true order by t.level desc", userConnected.id).fetch();
            render(userConnected, tasks);
        } else {
            redirect("Home.index");
        }
    }*/

    public static void newTask(Long level, String content) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (level != null && content != null) {
                Task task = new Task(userConnected.id, level, content);
                task.save();
                redirect("Tasks.index");
            }
            else
                render(userConnected);
        } else {
            redirect("Home.index");
        }
    }

    public static void delete(long id) {
        if (Security.isConnected()) {
                Task task = Task.find("byId", id).first();
                task.delete();
                redirect("Tasks.index");
        } else {
            redirect("Home.index");
        }
    }

    public static void done(long id) {
        if (Security.isConnected()) {
                Task task = Task.find("byId", id).first();
                task.done = true;
                task.save();
                redirect("Tasks.index");
        } else {
            redirect("Home.index");
        }
    }
}
