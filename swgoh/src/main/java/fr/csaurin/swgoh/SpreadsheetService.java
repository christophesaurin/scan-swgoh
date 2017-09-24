/**
 *
 */
package fr.csaurin.swgoh;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * @author U00I168
 */
public class SpreadsheetService {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Sheets API Java";

	/** Id Spreadsheet */
	private static final String spreadsheetId = "1oTdLC5FcoIMXozp5ydBvlXdKP_MJOqUPe_YDjNgEdpQ"; // "1KIy4KTlG_byrqzViOQuWDd3cuBrlDnwLkFCLk43sRDQ";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/sheets.googleapis.com-java-quickstart");

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	/** Global instance of the {@link FileDataStoreFactory}. */
	private FileDataStoreFactory dataStoreFactory;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private HttpTransport HTTP_TRANSPORT;

	private Sheets service;

	public SpreadsheetService() throws GeneralSecurityException, IOException {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize()).setApplicationName(APPLICATION_NAME).build();
	}

	private Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = SpreadsheetService.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(dataStoreFactory).setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	// private List<String> readCells(final String range) throws IOException {
	// List<String> results = new ArrayList<>();
	// ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
	// List<List<Object>> values = response.getValues();
	// for (List<Object> row : values) {
	// for (Object cell : row) {
	// results.add((String) cell);
	// }
	// }
	// return results;
	// }

	private ValueRange createValueRange(final Map<String, Map<String, Integer>> usersCharsStars,
			final Map<String, Map<String, Integer>> usersShipsStars) throws IOException {
		ValueRange valueRange = new ValueRange();
		valueRange.setRange("Rosters!A1");
		valueRange.setValues(new ArrayList<List<Object>>());

		List<String> usernames = new ArrayList<>(usersCharsStars.keySet());
		usernames.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
		List<String> charnames = new ArrayList<>(usersCharsStars.get("Leguman").keySet());
		charnames.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
		List<String> shipnames = new ArrayList<>(usersShipsStars.get("Leguman").keySet());
		shipnames.sort((s1, s2) -> s1.compareToIgnoreCase(s2));

		List<Object> header = new ArrayList<>();
		header.add("Personnages");
		for (String username : usernames) {
			header.add(username);
		}
		valueRange.getValues().add(header);

		for (String charname : charnames) {
			List<Object> line = new ArrayList<>();
			line.add(charname);
			for (String username : usernames) {
				line.add(usersCharsStars.get(username).get(charname));
			}
			valueRange.getValues().add(line);
		}
		for (String shipname : shipnames) {
			List<Object> line = new ArrayList<>();
			line.add(shipname);
			for (String username : usernames) {
				line.add(usersShipsStars.get(username).get(shipname));
			}
			valueRange.getValues().add(line);
		}

		return valueRange;
	}

	public void updateFile(final Map<String, Map<String, Integer>> usersCharsStars, final Map<String, Map<String, Integer>> usersShipsStars)
			throws IOException {
		ValueRange valueRange = createValueRange(usersCharsStars, usersShipsStars);
		service.spreadsheets().values().update(spreadsheetId, valueRange.getRange(), valueRange).setValueInputOption("RAW").execute();
	}
}
