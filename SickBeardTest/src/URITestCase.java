import java.net.URI;

import org.junit.Test;

import junit.framework.TestCase;


public class URITestCase {

	@Test
	public void PathValidationTest() throws Exception
	{
		// test extra slashes
		URI uri = new URI( "http", null, "nas-server", Integer.parseInt("8081"), "///api/e409e3b95faad0c41319e5a6bc8a3c7a/", "cmd=" + "sb.ping", null );
		System.out.println(uri.toString());
		// turns out it doesnt matter
	}
}
