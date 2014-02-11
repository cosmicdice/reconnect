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
    public static void index(long tab, String search, String tags, int level) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            // Liste des tâches validées
            List<Task> tasks = null;
            if (search != null) { // Recherche

                //découpage des tags
                if (tags == null) tags = "";
                tags = tags.replace(" ", "");
                ArrayList<String> tagsList = new ArrayList<String>(Arrays.asList(tags.split(",")));

                if (level == 0) {
                    tasks = Task.find("select t from Task as t where t.done=true order by t.level desc").fetch();
                }
                else {
                    tasks = Task.find("select t from Task as t where t.level < ? and t.done=true order by t.level desc", level).fetch();
                }

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
                    if (!keep) i.remove();
                }

            }
            else { // Liste normale
                tasks = Task.find("select t from Task as t where t.done=true order by t.level desc").fetch();
            }

            //Services de l'utilisateur en cours
            List<Task> myTasks = Task.find("select t from Task as t where t.owner=? order by t.level desc", userConnected.id).fetch();
            // Inscriptions
            List<Task> suscribedTasks = Task.find("select t from Task as t where t.owner=? order by t.level desc", userConnected.id).fetch();
            // Tâches à modérer
            List<Task> tasksToModerate = Task.find("select t from Task as t where t.done=false order by t.level desc").fetch();
            if (tab == 0) tab = 2;
            render(userConnected, tasks, tasksToModerate, suscribedTasks, myTasks, tab);
        } else {
            redirect("Home.index");
        }
    }

    public static void view(long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();

            if (task == null){
                redirect("Tasks.index");
            }
            if (task.credited == false && task.task_finished){
                    int total_credits = task.level * task.participants.size();
                    userConnected.addCredits(total_credits);
                    task.credited = true;
                    for (int i = 0; i < task.participants.size(); i++){
                        User participant = User.find("byId", task.participants.get(i)).first();
                        participant.addCredits(task.level);
                        participant.save();
                    }
            }
            List<String> listParticipants = new ArrayList<String>(600);
            for (int i = 0; i < task.participants.size(); i++){
                User participant = User.find("byId", task.participants.get(i)).first();
                String nameParticipant = participant.name;
                listParticipants.add(nameParticipant);
            }
            User owner = User.find("byId", task.owner).first();
            String owner_name = owner.name;
            render(task, listParticipants, owner_name);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void addAsParticipant(long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            if (task.participants.size() < task.participants_max && task.participants.contains(userConnected.id) == false && task.closed == false){
                task.addParticipant(userConnected.id);
                task.save();
            }
            Tasks.view(id);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void deleteParticipant(long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            if (task.participants.contains(userConnected.id) == true && task.closed == false){
                task.deleteParticipant(userConnected.id);
                task.save();
            }
            Tasks.view(id);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void participantDone(long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            if (task.participants.contains(userConnected.id)){
                task.addParticipant(userConnected.id);
                task.isFinished();
                task.save();
            }
            Tasks.view(id);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void ownerDone(long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            if (userConnected.id == task.owner){
                task.owner_done = true;
                task.isFinished();
                task.save();
            }
            Tasks.view(id);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void close(long id){
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            Task task = Task.find("byId", id).first();
            if (userConnected.id == task.owner && task.participants.size() > 0){
                task.closed = true;
                task.save();
            }
            Tasks.view(id);
        }
        else {
            redirect("Home.index");
        }
    }

    public static void newTask(int level, String content, String title, String tags, int participants_max) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (level > 0 && content != null) {

                //découpage des tags
                if (tags == null) tags = "";
                tags = tags.replace(" ", "");
                ArrayList<String> tagsList = new ArrayList<String>(Arrays.asList(tags.split(",")));

                Task task = new Task(userConnected.id, level, content, title, tagsList, participants_max);
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
