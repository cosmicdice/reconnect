package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Messages extends Controller {
    
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
          
				List<Message> inbox = Message.find("select m from Message as m where m.receiver=? order by m.id desc", userConnected.id).fetch();
    			List<User> inboxListOfUsers = User.findAll();
    			
				List<Message> outbox = Message.find("select m from Message as m where m.sender=? order by m.id desc", userConnected.id).fetch();
    			List<User> outboxListOfUsers = User.findAll();
    			
          render(userConnected, inbox, outbox, inboxListOfUsers, outboxListOfUsers);
        } else {
            redirect("Home.index");
        }
    }
	public static void show(Long id) {
        if (Security.isConnected()) {
            User userConnected = User.find("byUsername", Security.connected()).first();
            if (id != null) {
                Message mail = Message.find("byId", id).first();
				User user = User.find("byId", mail.sender).first();
                render(mail, user, userConnected);
            }
            else {
                redirect("Messages.index");
            }
        } else {
            redirect("Home.index");
        }
    }
	/*public static void inbox() {
        if (Security.isConnected()) {
			User userConnected = User.find("byUsername", Security.connected()).first();
			List<Message> mails = Message.find("byReceiver", userConnected.id).fetch();
			List<User> listOfUsers = User.findAll();
            render(userConnected, mails, listOfUsers);
        } else {
            redirect("Home.index");
        }
    }
	public static void outbox() {
        if (Security.isConnected()) {
			User userConnected = User.find("byUsername", Security.connected()).first();
			List<Message> mails = Message.find("bySender", userConnected.id).fetch();
			List<User> listOfUsers = User.findAll();
            render(userConnected, mails, listOfUsers);
        } else {
            redirect("Home.index");
        }
    }*/
	public static void newMessage(String username, String content, String subject, Long type, Long idGroup) {
        if (Security.isConnected()) {
			User userConnected = User.find("byUsername", Security.connected()).first();
			Long verif = new Long(3);
			if((username != null || idGroup != null) && content != null) {
				User receiver = User.find("byUsername", username).first();
				if (type == 1 && receiver != null) {
					Date date = new Date();
					Message message = new Message(userConnected.id, receiver.id, subject, date, content, type, null);
					message.save();
					redirect("Messages.index");
				}
				else if (type == 2 && idGroup != null){
					Date date = new Date();
					UsersGroup group = UsersGroup.find("byId", idGroup).first();
					for(Long idReceiver : group.members) {
						Message message = new Message(userConnected.id, idReceiver, subject, date, content, type, null);
						message.save();
					}
					redirect("Messages.index");
				}
			}
			else if(username != null && type.equals(verif) && subject != null) {
				render(userConnected, username, subject);
			}
			render(userConnected);
        } else {
            redirect("Home.index");
        }
    }
	public static void answer(String username, String subject) {
        if (Security.isConnected()) {
                if(username != null && subject != null) {
					Long type = new Long(3);
					String emptyString = null;
					Long emptyLong = null;
                	Messages.newMessage(username, emptyString, subject, type, emptyLong);
				}
        } else {
            redirect("Home.index");
        }
    }
	public static void delete(long id) {
        if (Security.isConnected()) {
                Message message = Message.find("byId", id).first();
                message.delete();
                redirect("Messages.inbox");
        } else {
            redirect("Home.index");
        }
    }
}