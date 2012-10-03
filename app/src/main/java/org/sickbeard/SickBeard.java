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

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.FutureEpisode.TimeEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickbeard.json.*;
import org.sickbeard.json.ShowJson.CacheStatusJson;
import org.sickbeard.json.deserializer.JsonBooleanDeserializer;
import org.sickbeard.json.type.JsonBoolean;
import org.sickbeard.net.SickAuthenticator;
import org.sickbeard.net.ssl.DefaultTrustManager;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.*;

public class SickBeard {
	
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	private static final String FAILURE = "failure";
	
	private boolean https = false;
	private String scheme;
	private String hostname;
	private String port;
	private String extraPath;
	private String path;
	
	private String user;
	private String password;
	
	private int apiVersion = 3;
	
	public SickBeard( String hostname, String port, String api, boolean https ) {
		this(hostname,port,api,https,"","","");
	}
	
	public SickBeard( String hostname, String port, String api, boolean https, String extraPath ) {
		this(hostname,port,api,https,extraPath,"","");
	}
	
	public SickBeard( String hostname, String port, String api, boolean https, String extraPath, String user, String password )
	{
		this.hostname = hostname;
		this.port = port;
		this.extraPath = "/" + extraPath + "/";
		this.path = this.extraPath + "/api/" + api + "/";
		try {
			this.https = https;
			this.scheme = "http";
			Authenticator.setDefault(new SickAuthenticator(user,password));
			if ( https ) {
				SSLContext ctx = SSLContext.getInstance("TLS");
		        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
		        scheme = "https";
			}
		} catch (Exception e){
			;
		}
		/***********************************************************
		 * ANDROID SPECIFIC START                                  *
		 ***********************************************************/
		// start a AsyncTask to try and find the actual api version number
		AsyncTask<Void,Void,CommandsJson> task = new AsyncTask<Void,Void,CommandsJson>(){
			@Override
			protected CommandsJson doInBackground(Void... arg0) {
				try {
					return SickBeard.this.sbGetCommands();
				} catch (Exception e) {
					Log.e("SickBeard", e.getMessage(), e);
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(CommandsJson result) {
				// do nothing because this is a network error
				if ( result == null )
					return;
				try {
					// if we get a version use it
					SickBeard.this.apiVersion = Integer.valueOf(result.api_version);
				} catch (NumberFormatException e) {
					// 2 was the odd float so assume its 2 if we cant get an int
					SickBeard.this.apiVersion = 2;
				}
		     }
		};
		task.execute();
		/***********************************************************
		 * ANDROID SPECIFIC END                                    *
		 ***********************************************************/
	}
	
	public SickBeard( SickBeard sick )
	{
		this( sick.scheme, sick.hostname, sick.port, sick.path, sick.extraPath, sick.user, sick.password );
	}
	
	private SickBeard( String scheme, String hostname, String port, String extraPath, String path, String user, String password ) {
		this.scheme = scheme;
		this.https = scheme.toLowerCase().compareTo("https") == 0 ? true : false;
		this.hostname = hostname;
		this.port = port;
		this.extraPath = extraPath;
		this.path = path;
		this.user = user;
		this.password = password;
	}
	
	public int getApiVersion()
	{
		return apiVersion;
	}
	
	public URI getServerUri() throws URISyntaxException
	{
		return new URI( scheme, null, hostname, Integer.parseInt(port), path, null, null );
	}
	
	public URI getServerUri( String command ) throws URISyntaxException
	{
		return new URI( scheme, null, hostname, Integer.parseInt(port), path, "cmd=" + command, null );
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
		EpisodeJson json = this.<EpisodeJson>commandData( builder.toString(), new TypeToken<JsonResponse<EpisodeJson>>(){}.getType() );
		return new Episode(json);
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
	
	public boolean episodeSetStatus( String tvdbid, List<SeasonEpisodePair> episodes, StatusEnum status ) throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append("episode.setstatus_0");
		for ( int i=1; i < episodes.size(); i++ ) {
			builder.append("|episode.setstatus_");
			builder.append(i);
		}
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		builder.append("&status=");
		builder.append(status.toJson());
		for ( int i=0; i < episodes.size(); i++ ) {
			SeasonEpisodePair p = episodes.get(i);
			builder.append("&episode.setstatus_");
			builder.append(i);
			builder.append(".season=");
			builder.append(p.season);
			builder.append("&episode.setstatus_");
			builder.append(i);
			builder.append(".episode=");
			builder.append(p.episode);
		}
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public ArrayList<String> exceptions( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("exceptions");
		builder.append("&tvdbid=");
		builder.append( tvdbid );

		return this.<ArrayList<String>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<String>>>(){}.getType() );
	}
	
	public FutureEpisodes future( FutureEpisodes.SortEnum sort ) throws Exception
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

		FutureEpisodes ret = this.<FutureEpisodes>commandData( builder.toString(), new TypeToken<JsonResponse<FutureEpisodes>>(){}.getType() );
		for ( FutureEpisode ep : ret.missed ) {
			ep.when = TimeEnum.MISSED;
		}
		for ( FutureEpisode ep : ret.today ) {
			ep.when = TimeEnum.TODAY;
		}
		for ( FutureEpisode ep : ret.soon ) {
			ep.when = TimeEnum.SOON;
		}
		for ( FutureEpisode ep : ret.later ) {
			ep.when = TimeEnum.LATER;
		}
		return ret;
	}
	
	public History history() throws Exception
	{
		ArrayList<HistoryJson> result = this.<ArrayList<HistoryJson>>commandData( "history", new TypeToken<JsonResponse<ArrayList<HistoryJson>>>(){}.getType() );
		return new History( result );
	}
	
	public History history( int limit ) throws Exception
	{
		StringBuilder builder = new StringBuilder("history");
		builder.append("&limit=");
		builder.append(limit);
		ArrayList<HistoryJson> result = this.<ArrayList<HistoryJson>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<HistoryJson>>>(){}.getType() );
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
	
	public Show show( String tvdbid, boolean fullSeasonListing ) throws Exception
	{
		if ( fullSeasonListing ) {
			StringBuilder builder = new StringBuilder("show|show.seasons");
			builder.append("&tvdbid=");
			builder.append(tvdbid);
			
			ShowWithFullSeasonListing results = this.<ShowWithFullSeasonListing>commandData( builder.toString(), new TypeToken<JsonResponse<ShowWithFullSeasonListing>>(){}.getType() );
			Show ret = new Show( results.show.data );
			ret.seasonList.clear();
			for ( Map.Entry<String, SeasonsJson> season : results.seasons.data.entrySet() ) {
				ret.seasonList.add( new Season( season.getKey(), season.getValue() ) );
			}
			return ret;
		} else {
			return show( tvdbid );
		}
	}
	
	public boolean showAddNew( String tvdbid ) throws Exception
	{
		return showAddNew(tvdbid,null,null,null,null,null);
	}
	
	public boolean showAddNew( String tvdbid, LanguageEnum language, Boolean seasonFolders, StatusEnum status, EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.addnew");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		if ( language != null ) {
			builder.append("&lang=");
			builder.append(language.getAbbrev());
		}
		if ( seasonFolders != null ) {
			// the option isnt called season folders anymore
			if ( apiVersion >= 3 ) {
				builder.append("&flatten_folders=");
			} else {
				builder.append("&season_folder=");
			}
			// if you pass me a boolean you better damn well have checked the version number
			builder.append( seasonFolders ? "1" : "0" );
		}
		if ( status != null ) {
			builder.append("&status=");
			builder.append(status.toJson());
		}
		if ( initial != null ) {
			builder.append("&initial=");
			Iterator<QualityEnum> iter = initial.iterator();
			if ( iter.hasNext() ) {
				builder.append(iter.next().toString().toLowerCase());
				while ( iter.hasNext() ) {
					builder.append("|");
					builder.append(iter.next().toString().toLowerCase());
				}
			}
		}
		if ( archive != null ) {
			builder.append("&archive=");
			Iterator<QualityEnum> iter = archive.iterator();
			if ( iter.hasNext() ) {
				builder.append(iter.next().toString().toLowerCase());
				while ( iter.hasNext() ) {
					builder.append("|");
					builder.append(iter.next().toString().toLowerCase());
				}
			}
		}
		
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
		
//		URI uri = new URI( serverUri.toString() + builder.toString() );
//		HttpURLConnection server = (HttpURLConnection)uri.toURL().openConnection();
//		server.connect();
//		return server.getInputStream();
		return this.getServerUri(builder.toString());
	}
	
	public URI showGetPoster( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.getposter");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
//		URI uri = new URI( serverUri.toString() + builder.toString() );
//		HttpURLConnection server = (HttpURLConnection)uri.toURL().openConnection();
//		server.connect();
//		return server.getInputStream();
		return this.getServerUri(builder.toString());
	}
	
	public GetQuality showGetQuality( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.getquality");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		GetQualityJson result = this.<GetQualityJson>commandData( builder.toString(), new TypeToken<JsonResponse<GetQualityJson>>(){}.getType() );
		
		return new GetQuality(result);
	}
	
	public boolean showPause( String tvdbid, Boolean pause ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.pause");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		if ( pause != null ) {
			builder.append("&pause=");
			builder.append( pause ? "1" : "0" );
		}
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean showPause( String[] tvdbids, Boolean pause ) throws Exception
	{
		StringBuilder builder = new StringBuilder();
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("show.pause_");
			builder.append(i);
			if ( i < tvdbids.length-1 )
				builder.append("|");
		}
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("&show.pause_");
			builder.append(i);
			builder.append(".tvdbid=");
			builder.append(tvdbids[i]);
		}
		if ( pause != null ) {
			builder.append("&pause=");
			builder.append( pause ? "1" : "0" );
		}
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean showRefresh( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.refresh");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean showRefresh( String[] tvdbids ) throws Exception
	{
		StringBuilder builder = new StringBuilder();
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("show.refresh_");
			builder.append(i);
			if ( i < tvdbids.length-1 )
				builder.append("|");
		}
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("&show.refresh_");
			builder.append(i);
			builder.append(".tvdbid=");
			builder.append(tvdbids[i]);
		}
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public List<Integer> showSeasonList( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.seasonlist");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		ArrayList<Integer> result = this.<ArrayList<Integer>>commandData( builder.toString(), new TypeToken<JsonResponse<ArrayList<Integer>>>(){}.getType() );
		
		return result;
	}
	
	public List<Season> showSeasons( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.seasons");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		SeasonsListJson result = this.<SeasonsListJson>commandData( builder.toString(), new TypeToken<JsonResponse<SeasonsListJson>>(){}.getType() );
		List<Season> ret = new ArrayList<Season>();
		for ( Map.Entry<String, SeasonsJson> entry : result.entrySet() ) {
			ret.add( new Season( entry.getKey(), entry.getValue() ) );
		}
		return ret;
	}
	
	public Season showSeasons( String tvdbid, String season ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.seasons");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		builder.append("&season=");
		builder.append(season);
		
		SeasonsJson result = this.<SeasonsJson>commandData( builder.toString(), new TypeToken<JsonResponse<SeasonsJson>>(){}.getType() );
		return new Season( season, result );
	}
	
	public boolean showSetQuality( String tvdbid, EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.setquality");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		if ( initial != null ) {
			builder.append("&initial=");
			Iterator<QualityEnum> iter = initial.iterator();
			if ( iter.hasNext() ) {
				builder.append(iter.next().toString().toLowerCase());
				while ( iter.hasNext() ) {
					builder.append("|");
					builder.append(iter.next().toString().toLowerCase());
				}
			}
		}
		if ( archive != null ) {
			builder.append("&archive=");
			Iterator<QualityEnum> iter = archive.iterator();
			if ( iter.hasNext() ) {
				builder.append(iter.next().toString().toLowerCase());
				while ( iter.hasNext() ) {
					builder.append("|");
					builder.append(iter.next().toString().toLowerCase());
				}
			}
		}
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean showUpdate( String tvdbid ) throws Exception
	{
		StringBuilder builder = new StringBuilder("show.update");
		builder.append("&tvdbid=");
		builder.append(tvdbid);
		
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
	}
	
	public boolean showUpdate( String[] tvdbids ) throws Exception
	{
		StringBuilder builder = new StringBuilder();
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("show.update_");
			builder.append(i);
			if ( i < tvdbids.length-1 )
				builder.append("|");
		}
		for ( int i=0; i < tvdbids.length; i++ ) {
			builder.append("&show.update_");
			builder.append(i);
			builder.append(".tvdbid=");
			builder.append(tvdbids[i]);
		}
		return this.<Object>commandSuccessful( builder.toString(), new TypeToken<JsonResponse<Object>>(){}.getType() );
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
	
	public SearchResults sbSearchTvDb( String query, LanguageEnum language ) throws Exception
	{
		StringBuilder builder = new StringBuilder("sb.searchtvdb");
		builder.append("&name=");
		builder.append( query );
		if ( language != null ) {
			builder.append("&lang=");
			builder.append( language.getAbbrev() );
		}
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
		return ( response.result.equals(SUCCESS) ? response.data : null );
	}

	private <T> JsonResponse<T> commandResponse( String command, Type type ) throws Exception
	{
		URI uri = this.getServerUri(command);
//		URI uri = new URI( serverUri.toString() + URLEncoder.encode(command) );
		HttpURLConnection server = null;
		if ( https ) {
			server = (HttpsURLConnection)uri.toURL().openConnection();
		} else {
			server = (HttpURLConnection)uri.toURL().openConnection();
		}
		server.setConnectTimeout(30000);
		BufferedReader reader = new BufferedReader( new InputStreamReader(server.getInputStream() ) );
		// TypeToken cannot figure out T so instead it must be supplied
		//Type type = new TypeToken< JSONResponse<T> >() {}.getType();
		GsonBuilder build = new GsonBuilder();
		StringBuilder sBuild = new StringBuilder();
		String input;
        while ((input = reader.readLine()) != null) 
            sBuild.append(input);
        reader.close();
        input = sBuild.toString();
		build.registerTypeAdapter(JsonBoolean.class, new JsonBooleanDeserializer() );
		JsonResponse<T> response = null;
		try {
			response = build.create().fromJson( input, type );
			tryExtractError(response);
			return response;
		} catch (Exception e) {
			// well something messed up
			// if this part messes up then something REALLY bad happened
			response = build.create().fromJson(input, new TypeToken<JsonResponse<Object>>(){}.getType());
			tryExtractError(response);
			// DO NOT RETURN AN ACTUAL OBJECT!!!!!
			// this makes the code in the UI confused
			return null;
		}
	}

	private <T> boolean commandSuccessful( String command, Type type ) throws Exception
	{
		return this.commandResponse(command, type).result.equals(SUCCESS);
	}

	private <T> void tryExtractError(JsonResponse<T> response) throws Exception {
		if ( response.result.compareTo(ERROR) == 0 || response.result.compareTo(FAILURE) == 0 ) {
			if ( response.message != null && response.message.length() > 0 )
				throw new Exception( response.message );
			else if ( response.data != null && response.data.toString().length() > 0 )
					throw new Exception( response.data.toString() );
			throw new Exception( "Unknown Error occurred ... Ut Oh.");
		}
	}
}
