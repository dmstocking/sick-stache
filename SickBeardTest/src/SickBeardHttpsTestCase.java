import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

import org.sickbeard.*;
import org.sickbeard.SickBeard.*;
import org.sickbeard.json.*;


public class SickBeardHttpsTestCase extends SickBeardTestCase {

	//BEFORE RUNNING THIS TEST CASE MAKE SURE YOU SET UP SICKBEARD TO BE HTTPS
	@Before
	public void SetUpTests(){
		// hostname or ip both work
		hostname = "nas-server";
		port = "8081";
		api = "e409e3b95faad0c41319e5a6bc8a3c7a";
		sick = new SickBeard(hostname, port, api, true);
	}

}
