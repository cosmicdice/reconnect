import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    /*@Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }*/

	@Test
	public void createAndRetrieveUser() {
		new User("toto@gmail.com", "password", "Toto", "toto").save();
		
		User toto = User.find("byEmail", "toto@gmail.com").first();
		
		assertNotNull(toto);
		assertEquals("Toto", toto.fullname);
	}
	
	@Test
	public void tryConnectAsUser() {
		new User("toto@gmail.com", "password", "Toto", "toto").save();
		
		assertNotNull(User.connect("toto", "password"));
		assertNull(User.connect("toto", "passwordd"));
	}

}
