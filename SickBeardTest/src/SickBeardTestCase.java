import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sickbeard.FutureEpisodes;
import org.sickbeard.FutureEpisodes.SortEnum;
import org.sickbeard.History;
import org.sickbeard.Logs;
import org.sickbeard.Season;
import org.sickbeard.Show;
import org.sickbeard.SickBeard;
import org.sickbeard.json.CommandsJson;
import org.sickbeard.json.MessageJson;

public class SickBeardTestCase {
	
	protected SickBeard sick;
	
	protected String hostname;
	protected String port;
	protected String api;
	
	@Before
	public void SetUpTests(){
		// hostname or ip both work
		hostname = "nas-server";
		port = "8081";
		api = "e409e3b95faad0c41319e5a6bc8a3c7a";
		sick = new SickBeard(hostname, port, api, false);
	}
	
	@Test
	public void commandsTest() throws Exception {
		CommandsJson response = sick.sbGetCommands();
		assertNotNull(response);
	}
	
	@Test
	public void futureTest() throws Exception {
		FutureEpisodes response = sick.future(SortEnum.DATE);
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
	public void showGetBanner() throws Exception {
		URI uri = sick.showGetBanner("71256");
		System.out.println(uri.toString());
	}
	
	@Test
	public void showGetPoster() throws Exception {
		URI uri = sick.showGetPoster("71256");
		System.out.println(uri.toString());
	}
	
	@Test
	public void showWithFullSeasonListingTest() throws Exception {
		Show response = sick.show("71256", true);
		assertNotNull(response);
	}
	
	@Test
	public void showsTest() throws Exception {
		ArrayList<Show> response = sick.shows();
		assertNotNull(response);
	}
	
	@Test
	public void showsSeasonsTest() throws Exception {
		Season response = sick.showSeasons("71256", "1");
		assertNotNull(response);
	}
	
	@Test
	public void showsSeasonListTest() throws Exception {
		List<Integer> response = sick.showSeasonList("71256");
		assertNotNull(response);
	}

}
