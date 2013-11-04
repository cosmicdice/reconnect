package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import javax.persistence.*;


import models.*;

public class Dashboard extends Controller {
    
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
            
            List<Task> tasks = Task.find("select t from Task as t where t.owner=? and t.done=false order by t.level desc", userConnected.id).fetch(5);
            int nbTasks = Task.find("select t from Task as t where t.owner=? and t.done=false order by t.level desc", userConnected.id).fetch().size();
            List<Message> inbox = Message.find("select m from Message as m where m.receiver=? order by m.id desc", userConnected.id).fetch(5);
			int nbInbox = Message.find("byReceiver", userConnected.id).fetch().size();
      		List<User> inboxListOfUsers = User.findAll();
      			
      		List<Status> statuses = new ArrayList<Status>();
			List<Status> listOfStatuses = Status.find("select s from Status as s order by s.id desc").fetch();
      		List<User> listOfUsers = User.findAll();
			List<UsersGroup> listOfGroups = UsersGroup.findAll();
			List<UsersGroup> groups = new ArrayList<UsersGroup>();
      		int i = 0;
      		for (Status st : listOfStatuses){
      			if (userConnected.contacts.indexOf(st.owner) != -1 || st.type == 2) {
      				statuses.add(st);
      				i++;
      				if (i >= 5)
      					break;
      			}
      		}
			for (UsersGroup gr : listOfGroups)
				if (gr.owner == userConnected.id)
					groups.add(gr);
				
      			
            render(userConnected, tasks, inbox, inboxListOfUsers, statuses, groups, listOfUsers, nbTasks, nbInbox);
        } else {
            redirect("Home.index");
        }
    }

}