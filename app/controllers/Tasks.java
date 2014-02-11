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
    
    public static void index(long tab, String search, String tags) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            
            // Liste des tâches validées
            List<Task> tasks = null;
            if (search != null) { // Recherche

                //découpage des tags
                if (tags == null) tags = "";
                tags = tags.replace(" ", "");
                ArrayList<String> tagsList = new ArrayList<String>(Arrays.asList(tags.split(",")));
                tasks = Task.find("select t from Task as t where t.done=true order by t.level desc").fetch();
                
                Iterator<Task> i = tasks.iterator();
                while (i.hasNext()) {
                    boolean keep = false;
                    Task task = i.next();

                    // tags
                    for (String tag : tagsList) {
                        if(tag.isEmpty() || task.tags.contains(tag)) {
                            keep = true;
                        }
                    }

                    //

                    if (!keep) i.remove();
                }

            }
            else { // Liste normale
                tasks = Task.find("select t from Task as t where t.done=true order by t.level desc").fetch();
            }
            
            //Services de l'utilisateur en cours
            List<Task> myTasks = Task.find("select t from Task as t where t.owner=? order by t.level desc", userConnected.id).fetch();
            
            // Tâches à modérer
            List<Task> tasksToModerate = Task.find("select t from Task as t where t.done=false order by t.level desc").fetch();
            
            if (tab == 0) tab = 2;
            render(userConnected, tasks, tasksToModerate, myTasks, tab);
        } else {
            redirect("Home.index");
        }
    }

    public static void view(long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            
            if (task == null)
                redirect("Tasks.index");
            else
                render(task);
            
        } else {
            redirect("Home.index");
        }
    }
    
    public static void newTask(Long level, String content, String title, String tags) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (level != null && content != null) {

                //découpage des tags
                if (tags == null) tags = "";
                tags = tags.replace(" ", "");
                ArrayList<String> tagsList = new ArrayList<String>(Arrays.asList(tags.split(",")));

                Task task = new Task(userConnected.id, level, content, title, tagsList);
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