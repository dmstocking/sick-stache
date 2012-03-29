import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import org.sickbeard.*;
import org.sickbeard.SickBeard.*;
import org.sickbeard.json.*;


public class SickBeardTestCase {
	
	private static SickBeard sick;
	
	private static String hostname;
	private static String port;
	private static String api;

	@BeforeClass
	public static void SetUpTests(){
		// hostname or ip both work
		hostname = "nas-server";
		port = "8081";
		api = "e409e3b95faad0c41319e5a6bc8a3c7a";
		sick = new SickBeard(hostname, port, api);
	}
	
	@Test
	public void commandsTest() throws Exception {
		CommandsJson response = sick.sbGetCommands();
		assertNotNull(response);
	}
	
	@Test
	public void futureTest() throws Exception {
		FutureJson response = sick.future(SortEnum.DATE);
		assertNotNull(response);
	}
	
	@Test
	public void historyTest() throws Exception {
		History response = sick.history();
		assertNotNull(response);
	}
	
	@Test
	public void logsTest() throws Exception {
		Logs logs = sick.logs();
		assertNotNull(logs);
		logs = sick.logs( Logs.LevelEnum.DEBUG );
		assertNotNull(logs);
	}
	
	@Test
	public void messageTest() throws Exception {
		ArrayList<MessageJson> response = sick.sbGetMessages();
		assertNotNull(response);
	}
	
	@Test
	public void pingTest() throws Exception {
		assertEquals(true, sick.sbPing());
	}
	
	@Test
	public void showTest() throws Exception {
		Show response = sick.show("71256");
		assertNotNull(response);
	}
	
	@Test
	public void showsTest() throws Exception {
		ArrayList<Show> response = sick.shows();
		assertNotNull(response);
	}
	
	@Test
	public void showsSeasonsTest() throws Exception {
		ArrayList<Episode> response = sick.showSeasons("71256", "1");
		assertNotNull(response);
	}
	
	@Test
	public void showsSeasonListTest() throws Exception {
		SeasonListJson response = sick.showSeasonList("71256");
		assertNotNull(response);
	}

}
