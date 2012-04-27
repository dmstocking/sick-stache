/*
 * 	libSickBeard is a java library for communication with sickbeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	libSickBeard is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickbeard;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.sickbeard.json.*;
import org.sickbeard.json.ShowJson.CacheStatusJson;
import org.sickbeard.json.deserializer.JsonBooleanDeserializer;
import org.sickbeard.json.type.JsonBoolean;
import org.sickbeard.net.ssl.DefaultTrustManager;



public class SickBeard {
	
	private static final String success = "success";
	
	public enum SortEnum {
		DATE, NETWORK, NAME
	}
	
	public enum StatusEnum {
		WANTED, SKIPPED, ARCHIVED, IGNORED, UNAIRED,SNATCHED, DOWNLOADED;
		
		public static String[] valuesSetableString()
		{
			StatusEnum[] s = StatusEnum.values();
			String[] ret = new String[4];
			for ( int i=0; i < ret.length; i++ ) {
				ret[i] = s[i].toJson();
			}
			return ret;
		}
		
		public static StatusEnum fromJson( String status )
		{
			return StatusEnum.valueOf(status.toUpperCase());
		}
		
		public String toJson()
		{
			return this.toString().toLowerCase();
		}
	}
	
	public enum TimeEnum {
		MISSED, TODAY, SOON, LATER
	}
	
	private URI serverUri;
	private boolean https = false;
	
	public SickBeard( String url, String port, String api, boolean https )
	{
		try {
			this.https = https;
			String protocol = "";
			if ( https ) {
				SSLContext ctx = SSLContext.getInstance("TLS");
		        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
//		        SSLContext.setDefault(ctx);
		        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
		        protocol = "https";
			} else {
				protocol ="http";
			}
			serverUri = new URI( protocol + "://" + url + ":" + port + "/api/" + api + "/?cmd=" );
		} catch (Exception e){
//			serverUri = new URI("http://192.168.0.101:8081/api/?cmd=");
			;
		}
	}
	
	public SickBeard( SickBeard sick )
	{
		try {
			this.serverUri = new URI( sick.serverUri.toString() );
		} catch (Exception e) {
			;
		}
	}
	
	public URI getServerUri() throws URISyntaxException
	{
		return new URI(serverUri.toString());
	}
	
	public Episode episode( String tvdbid, String season, String episode ) throws Exception
	{
		return this.episode( tvdbid, season, episode, false );
	}
	
	public Episode episode( String tvdbid, String season, String episode, boolean full_path ) throws Exception
	{
		StringBuilder builder = new StringBuilder("episode");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		builder.append("&season=");
		builder.append(season);
		builder.append("&episode=");
		builder.append(episode);
		builder.append("&full_path=");
		builder.append( ( full_path ? "1" : "0" ) );
		return this.<Episode>commandData( builder.toString(), new TypeToken<JsonResponse<Episode>>(){}.getType() );
	}
	
	public boolean episodeSearch( String tvdbid, String season, String episode ) throws Exception
	{
		StringBuilder builder = new StringBuilder("episode.search");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		builder.append("&season=");
		builder.append(season);
		builder.append("&episode=");
		builder.append(episode);
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean episodeSetStatus( String tvdbid, String season, String episode, StatusEnum status ) throws Exception
	{
		StringBuilder builder = new StringBuilder("episode.setstatus");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		builder.append("&season=");
		builder.append(season);
		builder.append("&episode=");
		builder.append(episode);
		builder.append("&status=");
		builder.append(status.toJson());
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public ArrayList<String> exceptions( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("exceptions");
		builder.append("&tvdbid=");
		builder.append( tvdbid );

		return this.<ArrayList<String>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<String>>>(){}.getType() );
	}
	
	public FutureJson future( SickBeard.SortEnum sort ) throws Exception
	{
		StringBuilder builder = new StringBuilder("future");
		builder.append("&sort=");
		switch ( sort ) {
		case DATE:
			builder.append("date");
			break;
		case NETWORK:
			builder.append("network");
			break;
		case NAME:
			builder.append("name");
			break;
		default:
			builder.append("date");
		}

		FutureJson ret = this.<FutureJson>commandData( builder.toString(), new TypeToken<JsonResponse<FutureJson>>(){}.getType() );
		for ( FutureEpisodeJson e : ret.missed ) {
			e.when = TimeEnum.MISSED;
		}
		for ( FutureEpisodeJson e : ret.today ) {
			e.when = TimeEnum.TODAY;
		}
		for ( FutureEpisodeJson e : ret.soon ) {
			e.when = TimeEnum.SOON;
		}
		for ( FutureEpisodeJson e : ret.later ) {
			e.when = TimeEnum.LATER;
		}
		return ret;
	}
	
	public History history() throws Exception
	{
		ArrayList<HistoryJson> result = this.<ArrayList<HistoryJson>>commandData( "history", new TypeToken<JsonResponse<ArrayList<HistoryJson>>>(){}.getType() );
		return new History( result );
	}
	
	public boolean historyClear() throws Exception
	{
		return this.<Object>commandSuccessful( "history.clear", new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean historyTrim() throws Exception
	{
		return this.<Object>commandSuccessful( "history.trim", new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public Logs logs() throws Exception
	{
		ArrayList<String> result =  this.<ArrayList<String>>commandData( "logs", new TypeToken<JsonResponse<ArrayList<String>>>(){}.getType() );
		return new Logs( result );
	}
	
	public Logs logs( Logs.LevelEnum minLevel ) throws Exception
	{
		StringBuilder builder = new StringBuilder("logs");
		builder.append("&min_level=");
		switch ( minLevel ) {
		case DEBUG:
			builder.append("debug");
			break;
		case INFO:
			builder.append("info");
			break;
		case WARNING:
			builder.append("warning");
			break;
		case ERROR:
			builder.append("error");
			break;
		default:
			builder.append("error");
			break;
		}
		ArrayList<String> result =  this.<ArrayList<String>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<String>>>(){}.getType() );
		return new Logs( result );
	}
	
	public Show show( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return new Show( this.<ShowJson>commandData( builder.toString(), new TypeToken<JsonResponse<ShowJson>>(){}.getType() ) );
	}
	
	public boolean showAddNew( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.addnew");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public CacheStatus showCache( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.cache");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return new CacheStatus( this.<CacheStatusJson>commandData( builder.toString(), new TypeToken<JsonResponse<CacheStatusJson>>(){}.getType() ) );
	}
	
	public boolean showDelete( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.delete");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public URI showGetBanner( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.getbanner");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		URI uri = new URI( serverUri.toString() + builder.toString() );
//		HttpURLConnection server = (HttpURLConnection)uri.toURL().openConnection();
//		server.connect();
//		return server.getInputStream();
		return uri;
	}
	
	public URI showGetPoster( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.getposter");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		URI uri = new URI( serverUri.toString() + builder.toString() );
//		HttpURLConnection server = (HttpURLConnection)uri.toURL().openConnection();
//		server.connect();
//		return server.getInputStream();
		return uri;
	}
	
	public SeasonListJson showSeasonList( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.seasonlist");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return new SeasonListJson( this.<ArrayList<Integer>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<Integer>>>(){}.getType() ) );
	}
	
	// because there is no season we get a different data structure back
//	public List<Episode> showSeasons( String tvdbid ) throws Exception
//	{
//		StringBuilder builder = new StringBuilder("show.seasons");
//		builder.append("&tvdbid=");
//		builder.append(tvdbid);
//		
//		HashMap<String,EpisodeJson> result = this.<HashMap<String,EpisodeJson>>commandData( builder.toString(), new TypeToken<JsonResponse<HashMap<String,EpisodeJson>>>(){}.getType() );
//		ArrayList<Episode> ret = new ArrayList<Episode>();
//		for ( Map.Entry<String, EpisodeJson> entry : result.entrySet() ) {
//			entry.getValue().episode = entry.getKey();
//			ret.add(new Episode(entry.getValue()));
//		}
//		// don't know if i should sort ...
//		Collections.sort(ret, new Comparator<Episode>() {
//			public int compare( Episode a, Episode b ) {
//				// only reason i do this is that i don't want "10" to be with "1"
//				return Integer.valueOf(a.episode).compareTo( Integer.valueOf(a.episode) );
//			}
//		});
//		return ret;
//	}
	
	public ArrayList<Episode> showSeasons( String tvdbid, String season ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.seasons");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		if ( season != null ) {
			builder.append("&season=");
			builder.append(season);
		}
		HashMap<String,EpisodeJson> result = this.<HashMap<String,EpisodeJson>>commandData( builder.toString(), new TypeToken<JsonResponse<HashMap<String,EpisodeJson>>>(){}.getType() );
		ArrayList<Episode> ret = new ArrayList<Episode>();
		for ( Map.Entry<String, EpisodeJson> entry : result.entrySet() ) {
			entry.getValue().episode = entry.getKey();
			ret.add(new Episode(entry.getValue()));
		}
		// don't know if i should sort ...
		Collections.sort(ret, new Comparator<Episode>() {
			public int compare( Episode a, Episode b ) {
				// only reason i do this is that i don't want "10" to be with "1"
				return Integer.valueOf(a.episode).compareTo( Integer.valueOf(a.episode) );
			}
		});
		return ret;
	}
	
	public ArrayList<Show> shows() throws Exception
	{
		HashMap<String,ShowJson> result = this.commandData( "shows", new TypeToken<JsonResponse<HashMap<String,ShowJson>>>(){}.getType() );
		for( Map.Entry<String, ShowJson> entry : result.entrySet() ) {
			entry.getValue().id = entry.getKey();
		}
		ArrayList<Show> ret = new ArrayList<Show>();
		for( ShowJson j : result.values() ) {
			ret.add( new Show(j) );
		}
		Collections.sort(ret, new Comparator<Show>() {
			public int compare( Show a, Show b ) {
				return a.showName.compareTo(a.showName);
			}
		});
		return ret;
	}
	
	public boolean sbForceSearch() throws Exception
	{
		return this.<Object>commandSuccessful( "sb.forcesearch", new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public CommandsJson sbGetCommands() throws Exception
	{
		return this.<CommandsJson>commandData( "sb", new TypeToken<JsonResponse<CommandsJson>>(){}.getType() );
	}
	
	public OptionsJson sbGetDefaults() throws Exception
	{
		return this.<OptionsJson>commandData( "sb", new TypeToken<JsonResponse<OptionsJson>>(){}.getType() );
	}
	
	public ArrayList<MessageJson> sbGetMessages() throws Exception
	{
		return this.<ArrayList<MessageJson>>commandData( "sb.getmessages", new TypeToken<JsonResponse<ArrayList<MessageJson>>>(){}.getType() );
	}

	public ArrayList<RootDirJson> sbGetRootDirs() throws Exception
	{
		return this.<ArrayList<RootDirJson>>commandData( "sb.getrootdirs", new TypeToken<JsonResponse<ArrayList<RootDirJson>>>(){}.getType() );
	}
	
	public boolean sbPauseBackLogSearch() throws Exception
	{
		return this.<Object>commandSuccessful( "sb.pausebacklog", new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean sbPing()
	{
		try {
			return this.<PingJson>commandSuccessful( "sb.ping", new TypeToken<JsonResponse<PingJson>>(){}.getType() );
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean sbRestart() throws Exception
	{
		return this.<Object>commandSuccessful( "sb.restart", new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public SearchResults sbSearchTvDb( String name ) throws Exception
	{
		StringBuilder builder = new StringBuilder("sb.searchtvdb");
		builder.append("&name=");
		builder.append( URLEncoder.encode( name, "UTF-8" ) );
		// having to use all these generics is kind of annoying
		TvDbResultsJson results = this.<TvDbResultsJson>commandData( builder.toString(), new TypeToken<JsonResponse<TvDbResultsJson>>(){}.getType() );
		SearchResults ret = new SearchResults();
		for ( TvDbResultJson json : results.results ) { // lol results of results :)
			ret.results.add( new SearchResultTvDb( json ) );
		}
		return ret;
	}
	
	public boolean sbSetDefaults( OptionsJson defaults ) throws Exception
	{
		return false;
	}
	
	// hard to test function XD
	public boolean sbShutdown() throws Exception
	{
		return this.<String>commandSuccessful( "sb.shutdown", new TypeToken<JsonResponse<String>>(){}.getType() );
	}

	private <T> T commandData( String command, Type type ) throws Exception
	{
		JsonResponse<T> response = this.commandResponse( command, type );
		return ( response.result.equals(success) ? response.data : null );
	}

	private <T> JsonResponse<T> commandResponse( String command, Type type ) throws Exception
	{
		URI uri = new URI( serverUri.toString() + command );
		HttpURLConnection server = null;
		if ( https ) {
			server = (HttpsURLConnection)uri.toURL().openConnection();
		} else {
			server = (HttpURLConnection)uri.toURL().openConnection();
		}
		server.setConnectTimeout(10000);
		Reader reader = new BufferedReader( new InputStreamReader(server.getInputStream() ) );
		// TypeToken cannot figure out T so instead it must be supplied
		//Type type = new TypeToken< JSONResponse<T> >() {}.getType();
		GsonBuilder build = new GsonBuilder();
		build.registerTypeAdapter(JsonBoolean.class, new JsonBooleanDeserializer() );
		JsonResponse<T> response = build.create().fromJson( reader, type );
		return response;
	}

	private <T> boolean commandSuccessful( String command, Type type ) throws Exception
	{
		return this.commandResponse(command, type).result.equals(success);
	}
}
