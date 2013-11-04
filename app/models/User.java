package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {
	
	//Champs relatifs au compte
	public String email;
	public String password;
	public String name;
	public String username;
	public int isAdmin;
	
	//Champs relatifs au profil 
	public String gender;
    public Date dateOfBirth;
    public String city;
    public String cellphone;
    public String phone;
    public String workphone;
	public String departement;
	public String picture;

    //Liste des contacts
	@Lob
    public ArrayList<Long> contacts = new ArrayList<Long>(600);
	
	public ArrayList<Long> getContacts(){
		return contacts;
	}
	public void setContacts(ArrayList<Long> contacts){
		this.contacts = contacts;
	}
	public void addContact(Long id){
		this.contacts.add(id);
	}
	public void deleteContact(Long id){
	    this.contacts.remove(this.contacts.indexOf(id));
	}
	
	@Lob
	public ArrayList<Long> contactsRequest = new ArrayList<Long>(600);
	
	public ArrayList<Long> getContactsRequest(){
		return contactsRequest;
	}
	public void setContactsRequest(ArrayList<Long> contactsRequest){
		this.contactsRequest = contactsRequest;
	}
	public void addContactRequest(Long id){
		this.contactsRequest.add(id);
	}
	public void deleteContactRequest(Long id){
	    this.contactsRequest.remove(this.contactsRequest.indexOf(id));
	}
	
	public void acceptContact(Long id){
	    this.addContact(id);
	    this.deleteContactRequest(id);
	}
	
	public User(String email, String password, String name, String username) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.username = username;
		this.dateOfBirth = new Date();
		this.contacts = new ArrayList<Long>(600);
		this.contactsRequest = new ArrayList<Long>(600);
	}

	public static Object connect(String username, String password) {
		return find("byUsernameAndPassword", username, password).first();
    }

}
