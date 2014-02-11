package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class App extends Controller {
    
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
            redirect("Tasks.index");
			User userConnected = User.find("byUsername", Security.connected()).first();
			List<Status> statuses = new ArrayList<Status>();
			List<Status> listOfStatuses = Status.find("select s from Status as s order by s.id desc").fetch();
			List<User> listOfUsers = User.findAll();
			int i = 0;
			for (Status st : listOfStatuses){
				if (userConnected.contacts.indexOf(st.owner) != -1 || st.type == 2) {
					statuses.add(st);
					i++;
					if (i >= 50)
						break;
				}
							
			}
            render(userConnected, statuses, listOfUsers);
        } else {
            redirect("Home.index");
        }
    }
    
}